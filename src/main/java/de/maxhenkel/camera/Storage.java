package de.maxhenkel.camera;

import net.minecraft.entity.player.EntityPlayerMP;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

public class Storage implements IStorage {

    static IStorage getStorageFile() {
        return new StorageFile();
    }

    static IStorage getStorageDb() {
        return new StorageDb();
    }


    @Override
    public void saveImage(EntityPlayerMP playerMp, UUID uuid, ByteBuffer data) {
        final IStorage storageFile = getStorageFile();
        final IStorage storageDb = getStorageDb();
        storageFile.saveImage(playerMp, uuid, data);
        storageDb.saveImage(playerMp, uuid, data);

    }

    @Override
    public Optional<ByteBuffer> loadImage(EntityPlayerMP playerMp, UUID uuid) {
        final IStorage storageFile = getStorageFile();
        final IStorage storageDb = getStorageDb();
        try {
            final Optional<ByteBuffer> optionalByteBufferFile = storageFile.loadImage(playerMp, uuid);
            final Optional<ByteBuffer> optionalByteBufferDb = storageDb.loadImage(playerMp, uuid);
            if (optionalByteBufferFile.isPresent()) {
                return optionalByteBufferFile;
            }
            if (optionalByteBufferDb.isPresent()) {
                return optionalByteBufferDb;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


//        if (!optionalByteBufferFile.isPresent()) try {
//            if (optionalByteBufferDb.isPresent()) {
//                return optionalByteBufferDb;
//            }
//            return optionalByteBufferFile;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return Optional.empty();
//    }
}
