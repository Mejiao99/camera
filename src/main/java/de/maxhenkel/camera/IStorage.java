package de.maxhenkel.camera;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public interface IStorage {


    /**
     * Saves an image. The world path is used to determine the save path when using local files.
     *
     */
    void saveImage(final UUID uuid, final ImageMetadata imageMetadata, final ByteBuffer data) throws Exception;

    /**
     * Returns a byte buffer of the image if the image exist or empty if the image doesn't exist.
     * Throws an exception for unexpected errors.
     */
    Optional<ImageAndMetadata> loadImage(final UUID uuid) throws Exception;

    /**
     * Returs a list of all available UUIDs in this storage.
     */
    Set<UUID> listUuids() throws Exception;

    void initialize() throws Exception;
}

