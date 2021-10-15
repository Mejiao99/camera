package de.maxhenkel.camera;

import net.minecraft.entity.player.EntityPlayerMP;

import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class StorageDb implements IStorage {

    @Override
    public void saveImage(final EntityPlayerMP playerMp, final UUID uuid, final ByteBuffer data) {
        try (final Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/camera_storage", "root", "aguacate978");
             final PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO t_camera_storage(uuid,raw_data,player_name,pos_x,pos_y,pos_z, world_name,time) VALUES(?,?,?,?,?,?,?,? )")
        ) {
            final String playerName = playerMp.getName();
            final double posX = playerMp.posX;
            final double posY = playerMp.posY;
            final double posZ = playerMp.posZ;
            final String playerWorld = playerMp.getServer().getName();

            final Blob blob = conn.createBlob();
            blob.setBytes(1, data.array());
            stmt.setString(1, uuid.toString());
            stmt.setBlob(2, blob);
            stmt.setString(3, playerName);
            stmt.setDouble(4, posX);
            stmt.setDouble(5, posY);
            stmt.setDouble(6, posZ);
            stmt.setString(7, playerWorld);
            stmt.setTimestamp(8, Timestamp.from(Instant.now()));
            stmt.execute();
            conn.commit();
        } catch (final SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public Optional<ByteBuffer> loadImage(final EntityPlayerMP playerMp, final UUID uuid) {
        try (final Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/camera_storage", "root", "aguacate978");
             final PreparedStatement stmt = conn.prepareStatement(
                     "select raw_data from t_camera_storage where uuid = ?;")) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                final Blob blob = resultSet.getBlob("raw_data");
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                return Optional.of(byteBuffer);
            }
            return Optional.empty();
        } catch (final SQLException throwables) {
            throwables.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Set<UUID> listUUID(EntityPlayerMP playerMp) throws Exception {
        final Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/camera_storage", "root", "aguacate978");
        final PreparedStatement stmt = conn.prepareStatement(
                "select uuid from t_camera_storage");
        {
            final ResultSet resultSet = stmt.executeQuery();
            final Set<UUID> uuids = new HashSet<>();
            while (resultSet.next()) {
                uuids.add(UUID.fromString(resultSet.getString(1)));
            }
            return uuids;
        }
    }
}

