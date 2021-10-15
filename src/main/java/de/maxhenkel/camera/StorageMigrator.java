package de.maxhenkel.camera;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StorageMigrator {
    private final IStorage from;
    private final IStorage to;

    public StorageMigrator(final IStorage from, final IStorage to) {
        this.from = from;
        this.to = to;
    }

    public void run(final Path worldPath) throws Exception {
        System.err.println("Migrating from:" + from.getClass().getSimpleName() + " to:" + to.getClass().getSimpleName());
        final Set<UUID> uuidsFrom = from.listUuids(worldPath);
        final Set<UUID> uuidsTo = to.listUuids(worldPath);

        System.err.println("From size: " + uuidsFrom.size() + " to size:" + uuidsTo.size());
        final Set<UUID> uuids = new HashSet<>(uuidsFrom);
        uuids.removeAll(uuidsTo);
        System.err.println("Number of images to migrate: " + uuids.size());

        for (final UUID uuid : uuids) {
            try {
                final ImageAndMetadata imageAndMetadata = from.loadImage(worldPath, uuid).get();
                to.saveImage(worldPath, uuid, imageAndMetadata.getImageMetadata(), imageAndMetadata.getByteBuffer());
            } catch (final Exception e) {
                System.err.println("Skipping. Error migrating image: " + uuid);
                e.printStackTrace();
            }
        }

    }
}
