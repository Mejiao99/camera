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
        final Optional<ByteBuffer> optPrimary = primaryStorage.loadImage(playerMp, uuid);
        final Optional<ByteBuffer> optSecondary = secondaryStorage.loadImage(playerMp, uuid);

        if (optPrimary.isPresent()) {
            return optPrimary;
        }
        if (optSecondary.isPresent()) {
            primaryStorage.saveImage(playerMp, uuid, optSecondary.get());
            return optPrimary;
        }
        return Optional.empty();
    }
}
