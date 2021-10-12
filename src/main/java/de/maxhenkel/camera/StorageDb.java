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
import java.util.Optional;
import java.util.UUID;

public class StorageDb implements IStorage {

    @Override
    public void saveImage(final EntityPlayerMP playerMP, final UUID uuid, final ByteBuffer data) {
        try (final Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/camera_storage", "root", "aguacate978");
             final PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO t_camera_storage(uuid,raw_data,player_name,player_position,world_name,time) VALUES(?, ?, ?, ?,?,? )")
        ) {
            final String playerName = playerMP.getName();
            final String playerPosition = playerMP.getPosition().toString();
            final String playerWorld = playerMP.getServer().getName();
            final Instant instant = Instant.now();
            final Timestamp current = Timestamp.from(instant);


            final Blob blob = conn.createBlob();
            blob.setBytes(1, data.array());
            stmt.setString(1, uuid.toString());
            stmt.setBlob(2, blob);
            stmt.setString(3, playerName);
            stmt.setString(4, playerPosition);
            stmt.setString(5, playerWorld);
            stmt.setTimestamp(6, current);
            stmt.execute();
            conn.commit();
        } catch (final SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public Optional<ByteBuffer> loadImage(final EntityPlayerMP playerMP, final UUID uuid) {
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
}

