package de.maxhenkel.camera;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;


public interface IStorage {

    void saveImage(Path worldPath, UUID uuid, ByteBuffer data);

    Optional<ByteBuffer> loadImage(Path worldPath, UUID uuid);
}

