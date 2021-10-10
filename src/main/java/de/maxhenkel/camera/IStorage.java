package de.maxhenkel.camera;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

interface IStorage {

    void saveImage(UUID uuid, ByteBuffer data);

    Optional<ByteBuffer> loadImage(UUID uuid);
}

