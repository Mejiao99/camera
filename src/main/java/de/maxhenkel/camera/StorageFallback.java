package de.maxhenkel.camera;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class StorageFallback implements IStorage {

    private final IStorage primaryStorage;
    private final IStorage secondaryStorage;

    public StorageFallback(final IStorage primaryStorage, final IStorage secondaryStorage) {
        this.primaryStorage = primaryStorage;
        this.secondaryStorage = secondaryStorage;
    }

    @Override
    public void saveImage(final UUID uuid, final ImageMetadata metadata, final ByteBuffer data)
            throws Exception {
        primaryStorage.saveImage(uuid, metadata, data);
        secondaryStorage.saveImage(uuid, metadata, data);
    }

    @Override
    public Optional<ImageAndMetadata> loadImage(final UUID uuid) throws Exception {
        final Optional<ImageAndMetadata> optPrimary = primaryStorage.loadImage(uuid);
        if (optPrimary.isPresent()) {
            return optPrimary;
        }

        System.err.println("Image not found in primary source. Retrieving it from fallback: " + uuid);
        final Optional<ImageAndMetadata> optSecondary = secondaryStorage.loadImage(uuid);
        if (!optSecondary.isPresent()) {
            return Optional.empty();
        }
        final ImageAndMetadata imageAndMetadata = optSecondary.get();
        primaryStorage.saveImage(uuid, imageAndMetadata.getImageMetadata(), imageAndMetadata.getByteBuffer());
        return optSecondary;
    }

    @Override
    public Set<UUID> listUuids() throws Exception {
        final Set<UUID> uuids = new HashSet<>();
        uuids.addAll(primaryStorage.listUuids());
        uuids.addAll(secondaryStorage.listUuids());
        return uuids;
    }

    @Override
    public void initialize() throws Exception {
        new StorageMigrator(primaryStorage, secondaryStorage)
                .run();
    }


}

