package de.maxhenkel.camera;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class StorageDb implements IStorage {

    @Override
    public void saveImage(Path worldPath, UUID uuid, ByteBuffer data) {
        try (final Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/camera_storage", "root", "aguacate978");
             final PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO t_camera_storage(uuid,raw_data) VALUES(?, ?)")
        ) {
            final Blob blob = conn.createBlob();
            blob.setBytes(1, data.array());
            stmt.setString(1, uuid.toString());
            stmt.setBlob(2, blob);
            stmt.execute();
            conn.commit();
        } catch (final SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public Optional<ByteBuffer> loadImage(Path worldPath, UUID uuid) {
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

