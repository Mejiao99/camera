package de.maxhenkel.camera;

import net.minecraft.entity.player.EntityPlayerMP;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

public class StorageFallback implements IStorage {

    IStorage primaryStorage;
    IStorage secondaryStorage;


    @Override
    public void saveImage(final EntityPlayerMP playerMp, final UUID uuid, final ByteBuffer data) {
        primaryStorage.saveImage(playerMp, uuid, data);
        secondaryStorage.saveImage(playerMp, uuid, data);
    }

    @Override
    public Optional<ByteBuffer> loadImage(final EntityPlayerMP playerMp, final UUID uuid) {
        final Optional<ByteBuffer> optionalPrimary = primaryStorage.loadImage(playerMp, uuid);
        final Optional<ByteBuffer> optionalSecondary = secondaryStorage.loadImage(playerMp, uuid);
        if (!optionalPrimary.isPresent()) {
            if (optionalSecondary.isPresent()) {
                primaryStorage.saveImage(playerMp, uuid, optionalSecondary.get());
                return optionalPrimary;
            }

        }
        return Optional.empty();
    }
}
