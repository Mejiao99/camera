package de.maxhenkel.camera;

import net.minecraft.entity.player.EntityPlayerMP;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public interface IStorage {

    void saveImage(EntityPlayerMP playerMp, UUID uuid, ByteBuffer data);

    Optional<ByteBuffer> loadImage(EntityPlayerMP playerMp, UUID uuid);

    Set<UUID> listUUID(EntityPlayerMP playerMp) throws Exception;
}

