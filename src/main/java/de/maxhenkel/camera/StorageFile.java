package de.maxhenkel.camera;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class StorageFile implements IStorage {
    private Path worldPath;

    public StorageFile(Path worldPath) {
        this.worldPath = worldPath;
    }


    @Override
    public void saveImage(final UUID uuid, final ImageMetadata metadata, final ByteBuffer data)
            throws Exception {
        final Path rootPath = worldPath.resolve("camera_images");

        final Path imagePath = rootPath.resolve(uuid + ".png");
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, data.array());

        final Path jsonPath = rootPath.resolve(uuid + ".json");
        Files.write(jsonPath, new Gson().toJson(metadata).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Optional<ImageAndMetadata> loadImage(final UUID uuid) throws Exception {
        final Path rootPath = worldPath.resolve("camera_images");
        final Path imagePath = rootPath.resolve(uuid + ".png");
        if (Files.notExists(imagePath)) {
            return Optional.empty();
        }
        final Path jsonPath = rootPath.resolve(uuid + ".json");
        final ImageMetadata metadata;
        if (Files.exists(jsonPath)) {
            final String json = new String(Files.readAllBytes(jsonPath), StandardCharsets.UTF_8);
            metadata = new Gson().fromJson(json, ImageMetadata.class);
        } else {
            metadata = new ImageMetadata();
        }
        return Optional.of(new ImageAndMetadata(metadata, ByteBuffer.wrap(Files.readAllBytes(imagePath))));
    }

    @Override
    public Set<UUID> listUuids() throws Exception {
        final Path path = worldPath.resolve("camera_images");
        if (Files.notExists(path)) {
            return Collections.emptySet();
        }
        return Files.walk(path)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(name -> StringUtils.endsWithIgnoreCase(name, ".png"))
                .map(name -> StringUtils.substringBeforeLast(name, "."))
                .map(this::strToUuid)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

    }

    private UUID strToUuid(String strUuid) {
        try {
            return UUID.fromString(strUuid);

        } catch (final Exception e) {
            System.err.println("Cannot parse UUID: " + strUuid);
            return null;
        }
    }

    @Override
    public void initialize() throws Exception {

    }

}
