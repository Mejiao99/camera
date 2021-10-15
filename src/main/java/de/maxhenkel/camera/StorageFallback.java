package de.maxhenkel.camera;

import net.minecraft.entity.player.EntityPlayerMP;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class StorageFallback implements IStorage {

    private final IStorage primaryStorage;
    private final IStorage secondaryStorage;

    public StorageFallback(final IStorage primaryStorage, final IStorage secondaryStorage) {
        this.primaryStorage = primaryStorage;
        this.secondaryStorage = secondaryStorage;
    }

    @Override
    public void saveImage(final EntityPlayerMP playerMp, final UUID uuid, final ByteBuffer data) {
        primaryStorage.saveImage(playerMp, uuid, data);
        secondaryStorage.saveImage(playerMp, uuid, data);
    }

    @Override
    public Optional<ByteBuffer> loadImage(final EntityPlayerMP playerMp, final UUID uuid) {
        final Optional<ByteBuffer> optPrimary = primaryStorage.loadImage(playerMp, uuid);
        if (optPrimary.isPresent()) {
            return optPrimary;
        }

        final Optional<ByteBuffer> optSecondary = secondaryStorage.loadImage(playerMp, uuid);
        if (optSecondary.isPresent()) {
            primaryStorage.saveImage(playerMp, uuid, optSecondary.get());
            return optPrimary;
        }
        return Optional.empty();
    }

    @Override
    public Set<UUID> listUUID(EntityPlayerMP playerMp) {
        return null;
    }
}
