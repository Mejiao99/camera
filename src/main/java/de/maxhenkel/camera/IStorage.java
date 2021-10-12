package de.maxhenkel.camera;

import net.minecraft.entity.player.EntityPlayerMP;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;


public interface IStorage {

    void saveImage(EntityPlayerMP playerMP, UUID uuid, ByteBuffer data);

    Optional<ByteBuffer> loadImage(EntityPlayerMP playerMP, UUID uuid);
}

