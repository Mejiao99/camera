package de.maxhenkel.camera;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class StorageFallback implements IStorage {

    private final IStorage primaryStorage;
    private final IStorage secondaryStorage;
    private boolean migrated;

    public StorageFallback(final IStorage primaryStorage, final IStorage secondaryStorage) {
        this.primaryStorage = primaryStorage;
        this.secondaryStorage = secondaryStorage;
    }

    @Override
    public void saveImage(final Path worldPath, final UUID uuid, final ImageMetadata metadata, final ByteBuffer data)
            throws Exception {
        migrate(worldPath);
        primaryStorage.saveImage(worldPath, uuid, metadata, data);
        secondaryStorage.saveImage(worldPath, uuid, metadata, data);
    }

    @Override
    public Optional<ImageAndMetadata> loadImage(final Path worldPath, final UUID uuid) throws Exception {
        migrate(worldPath);
        final Optional<ImageAndMetadata> optPrimary = primaryStorage.loadImage(worldPath, uuid);
        if (optPrimary.isPresent()) {
            return optPrimary;
        }

        System.err.println("Image not found in primary source. Retrieving it from fallback: " + uuid);
        final Optional<ImageAndMetadata> optSecondary = secondaryStorage.loadImage(worldPath, uuid);
        if (!optSecondary.isPresent()) {
            return Optional.empty();
        }
        final ImageAndMetadata imageAndMetadata = optSecondary.get();
        primaryStorage.saveImage(worldPath, uuid, imageAndMetadata.getImageMetadata(), imageAndMetadata.getByteBuffer());
        return optSecondary;
    }

    @Override
    public Set<UUID> listUuids(final Path worldPath) throws Exception {
        final Set<UUID> uuids = new HashSet<>();
        uuids.addAll(primaryStorage.listUuids(worldPath));
        uuids.addAll(secondaryStorage.listUuids(worldPath));
        return uuids;
    }

    private void migrate(final Path worldPath) throws Exception {
        if (migrated) {
            return;
        }
        synchronized (this) {
            if (migrated) {
                return;
            }
            new StorageMigrator(primaryStorage, secondaryStorage)
                    .run(worldPath);
            migrated = true;
        }
    }

}

