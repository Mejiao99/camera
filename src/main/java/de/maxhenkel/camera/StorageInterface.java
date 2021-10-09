package de.maxhenkel.camera;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

interface StorageInterface {

    File getImageFile(File file, UUID uuid);

    void saveImage(File file, ByteBuffer data, UUID uuid);

    Optional<ByteBuffer> loadImage();
}

