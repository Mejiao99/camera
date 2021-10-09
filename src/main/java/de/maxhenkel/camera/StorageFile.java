package de.maxhenkel.camera;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.UUID;

public class StorageFile implements StorageInterface {

    @Override
    public File getImageFile(File file, UUID uuid) {
        File imageFolder = new File(file, "camera_images");
        return new File(imageFolder, uuid.toString() + ".png");
    }

    @Override
    public void saveImage(File file, ByteBuffer data, UUID uuid) {
        File image = getImageFile(file, uuid);
        image.mkdirs();
        ImageIO.write((RenderedImage) data, "png", image);
    }

    @Override
    public void loadImage() {
    }
}

