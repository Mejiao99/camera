package de.maxhenkel.camera;

import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class StorageFile implements IStorage {

    @Override
    public void saveImage(final EntityPlayerMP playerMp, final UUID uuid, final ByteBuffer data) {
        final Path worldPath = playerMp.getServerWorld().getSaveHandler().getWorldDirectory().toPath();
        final Path path = worldPath.resolve("camera_images").resolve(uuid.toString() + ".png");
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, data.array());
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Optional<ByteBuffer> loadImage(final EntityPlayerMP playerMp, final UUID uuid) {
        final Path worldPath = playerMp.getServerWorld().getSaveHandler().getWorldDirectory().toPath();
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

    @Override
    public Set<UUID> listUUID(EntityPlayerMP playerMp) throws Exception {
        final Path worldPath = playerMp.getServerWorld().getSaveHandler().getWorldDirectory().toPath();
        final Path path = worldPath.resolve("camera_images");
        return Files.walk(path)
                .map(namePath -> namePath.getFileName())
                .map(fileNamePath -> fileNamePath.toString())
                .filter(name -> name.endsWith(".png"))
                .map(name -> StringUtils.substringBeforeLast(name, "."))
                .map(uuidStr -> UUID.fromString(uuidStr))
                .collect(Collectors.toSet());

    }

}
