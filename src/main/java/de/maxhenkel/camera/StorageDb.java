package de.maxhenkel.camera;

import de.maxhenkel.camera.proxy.CommonProxy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class StorageDb implements IStorage {
    private boolean isCreated;

    @Override
    public void saveImage(final Path worldPath, final UUID uuid, final ImageMetadata metadata, final ByteBuffer data)
            throws Exception {
        initialize();
        try (final Connection conn = DriverManager.getConnection(CommonProxy.connectionUrl, CommonProxy.dbUser, CommonProxy.dbPassword);
             final PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO t_camera_storage(uuid,raw_data,player_name,pos_x,pos_y,pos_z, world_name,time) VALUES(?,?,?,?,?,?,?,?)")
        ) {

            final Blob blob = conn.createBlob();
            blob.setBytes(1, data.array());
            stmt.setString(1, uuid.toString());
            stmt.setBlob(2, blob);
            stmt.setString(3, Optional.ofNullable(metadata).map(ImageMetadata::getPlayerName).orElse(""));
            stmt.setDouble(4, Optional.ofNullable(metadata).map(ImageMetadata::getPosX).orElse(0.0));
            stmt.setDouble(5, Optional.ofNullable(metadata).map(ImageMetadata::getPosY).orElse(0.0));
            stmt.setDouble(6, Optional.ofNullable(metadata).map(ImageMetadata::getPosZ).orElse(0.0));
            stmt.setString(7, Optional.ofNullable(metadata).map(ImageMetadata::getWorldName).orElse(""));
            stmt.setTimestamp(8,
                    Timestamp.from(Optional.ofNullable(metadata)
                            .map(ImageMetadata::getTimestamp)
                            .orElseGet(Instant::now)));
            stmt.execute();
            conn.commit();
        }

    }

    @Override
    public Optional<ImageAndMetadata> loadImage(final Path worldPath, final UUID uuid) throws Exception {
        initialize();
        try (final Connection conn = DriverManager.getConnection(CommonProxy.connectionUrl, CommonProxy.dbUser, CommonProxy.dbPassword);
             final PreparedStatement stmt = conn.prepareStatement(
                     "select raw_data,player_name,pos_x,pos_y,pos_z,world_name,time" +
                             " from t_camera_storage" +
                             " where uuid = ?")) {
            stmt.setString(1, uuid.toString());
            final ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                // No image found with that id
                return Optional.empty();
            }
            final ImageMetadata metatada = new ImageMetadata();
            metatada.setPlayerName(StringUtils.defaultString(resultSet.getString("player_name")));
            metatada.setWorldName(StringUtils.defaultString(resultSet.getString("world_name")));
            metatada.setPosX(resultSet.getDouble("pos_x"));
            metatada.setPosY(resultSet.getDouble("pos_y"));
            metatada.setPosZ(resultSet.getDouble("pos_z"));
            metatada.setTimestamp(
                    Optional.ofNullable(resultSet.getTimestamp("time"))
                            .map(Timestamp::toInstant)
                            .orElseGet(Instant::now));
            final ByteBuffer byteBuffer = ByteBuffer.wrap(
                    IOUtils.toByteArray(
                            resultSet.getBlob("raw_data")
                                    .getBinaryStream()));
            return Optional.of(
                    new ImageAndMetadata(
                            metatada,
                            byteBuffer));
        }
    }

    @Override
    public Set<UUID> listUuids(final Path worldPath) throws Exception {
        try (final Connection conn = DriverManager.getConnection(CommonProxy.connectionUrl, CommonProxy.dbUser, CommonProxy.dbPassword);
             final PreparedStatement stmt = conn.prepareStatement("select uuid from t_camera_storage")) {
            initialize();
            final ResultSet resultSet = stmt.executeQuery();
            final Set<UUID> uuids = new HashSet<>();
            final int uuidIdx = resultSet.findColumn("uuid");
            while (resultSet.next()) {
                uuids.add(UUID.fromString(resultSet.getString(uuidIdx)));
            }
            return uuids;
        }
    }

    private void createTable() throws Exception {
        System.out.println("createTable executed");
        try (final Connection conn = DriverManager.getConnection(CommonProxy.connectionUrl, CommonProxy.dbUser, CommonProxy.dbPassword);
             final PreparedStatement stmt = conn.prepareStatement(
                     "create table if not exists t_camera_storage\n" +
                             "(\n" +
                             "    uuid        varchar(64)  not null\n" +
                             "        primary key,\n" +
                             "    raw_data    longblob     null,\n" +
                             "    player_name varchar(256) null,\n" +
                             "    pos_x       double       null,\n" +
                             "    pos_y       double       null,\n" +
                             "    pos_z       double       null,\n" +
                             "    world_name  varchar(256) null,\n" +
                             "    time        timestamp    null\n" +
                             ");"
             )) {
            stmt.execute();
            conn.commit();
        }
        System.out.println("createTable executed-1");
    }

    private void initialize() throws Exception {
        if (isCreated) {
            return;
        }
        synchronized (this) {
            if (isCreated) {
                return;
            }
            new StorageDb().createTable();
            isCreated = true;
        }
    }
}



