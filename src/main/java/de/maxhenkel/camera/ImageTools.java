package de.maxhenkel.camera;

import net.minecraft.entity.player.EntityPlayerMP;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
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

    static IStorage getStorage() {
        return new StorageDb();
    }


    public static File getImageFile(EntityPlayerMP playerMP, UUID uuid) {
        File imageFolder = new File(playerMP.getServerWorld().getSaveHandler().getWorldDirectory(), "camera_images");
        return new File(imageFolder, uuid.toString() + ".png");
    }

    public static void saveImage(EntityPlayerMP playerMP, UUID uuid, BufferedImage bufferedImage) throws IOException {
        final IStorage storage = getStorage();
        final byte[] bytes = toBytes(bufferedImage);
        storage.saveImage(playerMP, uuid, ByteBuffer.wrap(bytes));
    }


    public static BufferedImage loadImage(EntityPlayerMP playerMP, UUID uuid) throws IOException {
        final IStorage storage = getStorage();

        final Optional<ByteBuffer> optionalByteBuffer = storage.loadImage(playerMP, uuid);

        if (!optionalByteBuffer.isPresent()) {
            throw new IOException("byteBuffer isn't present");
        }
        final ByteBuffer byteBuffer = optionalByteBuffer.get();
        final byte[] bytes = byteBuffer.array();
        final BufferedImage bufferedImage = fromBytes(bytes);

        return bufferedImage;
    }

}
