package de.maxhenkel.camera;

import java.nio.ByteBuffer;

public class ImageAndMetadata {
    private ImageMetadata imageMetadata;
    private ByteBuffer byteBuffer;

    public ImageAndMetadata(ImageMetadata imageMetadata, ByteBuffer byteBuffer) {
        this.imageMetadata = imageMetadata;
        this.byteBuffer = byteBuffer;
    }

    public ImageMetadata getImageMetadata() {
        return imageMetadata;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }
}
