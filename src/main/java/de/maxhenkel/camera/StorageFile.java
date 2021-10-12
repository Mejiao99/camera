package de.maxhenkel.camera;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class StorageFile implements IStorage {

    @Override
    public void saveImage(final Path worldPath, final UUID uuid, final ByteBuffer data) {
        final Path path = worldPath.resolve("camera_images").resolve(uuid.toString() + ".png");
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, data.array());
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Optional<ByteBuffer> loadImage(Path worldPath, UUID uuid) {
        final Path path = worldPath.resolve("camera_images").resolve(uuid.toString() + ".png");
        try {
            final byte[] bytes = Files.readAllBytes(path);
            final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            return Optional.of(byteBuffer);
        } catch (final IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
