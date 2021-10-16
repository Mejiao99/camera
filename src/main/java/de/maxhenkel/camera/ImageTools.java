package de.maxhenkel.camera;

import de.maxhenkel.camera.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class ImageTools {

    public static byte[] toBytes(BufferedImage image) throws IOException {
        ImageIO.setUseCache(false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        byte[] data = baos.toByteArray();
        baos.close();
        return data;
    }

    public static BufferedImage fromBytes(byte[] data) throws IOException {
        ImageIO.setUseCache(false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedImage image = ImageIO.read(bais);
        bais.close();
        return image;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static void saveImage(EntityPlayerMP playerMP, UUID uuid, BufferedImage bufferedImage) throws IOException {
        final byte[] bytes = toBytes(bufferedImage);

        final ImageMetadata imageMetadata = new ImageMetadata();
        imageMetadata.setPlayerName(playerMP.getName());
        imageMetadata.setWorldName(playerMP.getServerWorld().getWorldInfo().getWorldName());
        imageMetadata.setPosX(playerMP.posX);
        imageMetadata.setPosY(playerMP.posY);
        imageMetadata.setPosZ(playerMP.posZ);
        imageMetadata.setTimestamp(Instant.now());
        try {
            CommonProxy.storage.saveImage(
                    uuid,
                    imageMetadata,
                    ByteBuffer.wrap(bytes));
        } catch (final Exception e) {
            throw new IOException("Error saving: " + uuid, e);
        }
    }


    public static BufferedImage loadImage(final EntityPlayerMP playerMP, final UUID uuid) throws IOException {

        final Optional<ImageAndMetadata> optImageAndMetadata;
        try {
            optImageAndMetadata = CommonProxy.storage.loadImage(
                    uuid);
        } catch (final Exception e) {
            throw new IOException("Error loading: " + uuid, e);
        }

        if (!optImageAndMetadata.isPresent()) {
            throw new IOException("Image not found: " + uuid);
        }
        return fromBytes(optImageAndMetadata.get().getByteBuffer().array());
    }

}
