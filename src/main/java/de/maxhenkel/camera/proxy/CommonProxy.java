package de.maxhenkel.camera.proxy;

import de.maxhenkel.camera.IStorage;
import de.maxhenkel.camera.Main;
import de.maxhenkel.camera.StorageDb;
import de.maxhenkel.camera.StorageFallback;
import de.maxhenkel.camera.StorageFile;
import de.maxhenkel.camera.gui.GuiHandler;
import de.maxhenkel.camera.net.MessageImage;
import de.maxhenkel.camera.net.MessageImageUnavailable;
import de.maxhenkel.camera.net.MessagePartialImage;
import de.maxhenkel.camera.net.MessageRequestImage;
import de.maxhenkel.camera.net.MessageTakeImage;
import de.maxhenkel.camera.net.MessageUpdateImage;
import de.maxhenkel.camera.net.PacketManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.nio.file.Path;
import java.text.SimpleDateFormat;

public class CommonProxy {

    public static SimpleDateFormat imageDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    public static long imageCooldown = 5000;

    public static String connectionUrl = null;
    public static String dbUser = null;
    public static String dbPassword = null;


    public static SimpleNetworkWrapper simpleNetworkWrapper;
    public static PacketManager manager;
    public static IStorage storage;

    public void preinit(final FMLPreInitializationEvent event) {

        initConfig(event);

        CommonProxy.simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Main.MODID);
        CommonProxy.manager = new PacketManager();
        CommonProxy.simpleNetworkWrapper.registerMessage(MessagePartialImage.class, MessagePartialImage.class, 0, Side.SERVER);
        CommonProxy.simpleNetworkWrapper.registerMessage(MessageTakeImage.class, MessageTakeImage.class, 1, Side.CLIENT);
        CommonProxy.simpleNetworkWrapper.registerMessage(MessageRequestImage.class, MessageRequestImage.class, 2, Side.SERVER);
        CommonProxy.simpleNetworkWrapper.registerMessage(MessageImage.class, MessageImage.class, 3, Side.CLIENT);
        CommonProxy.simpleNetworkWrapper.registerMessage(MessageUpdateImage.class, MessageUpdateImage.class, 4, Side.CLIENT);
        CommonProxy.simpleNetworkWrapper.registerMessage(MessageImageUnavailable.class, MessageImageUnavailable.class, 5, Side.CLIENT);
    }

    public void init(final FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance(), new GuiHandler());

    }

    public void postinit(final FMLPostInitializationEvent event) {

    }

    private void initConfig(final FMLPreInitializationEvent event) {
        try {
            Configuration config = new Configuration(event.getSuggestedConfigurationFile());
            config.load();

            String format = config.getString("image_date_format", "camera", "MM/dd/yyyy HH:mm", "The format the date will be displayed on the image");
            imageCooldown = config.getInt("image_cooldown", "camera", 5000, 100, Integer.MAX_VALUE, "The time in milliseconds the camera will be on cooldown after taking an image");
            imageDateFormat = new SimpleDateFormat(format);

            connectionUrl = config.getString("tortilla_camera:jdbc_url", "camera", "FIX ME tortilla_camera:jdbc_url", "Use jdbc:mariadb://localhost:3306/camera_storage");
            dbUser = config.getString("tortilla_camera:db_user", "camera", "root", "user");
            dbPassword = config.getString("tortilla_camera:db_pass", "camera", "admin", "pass");

            System.err.println("tortilla_camera:jdbc_url. " + connectionUrl + " - side:" + event.getSide());

            config.save();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    public void serverStarting(final FMLServerStartingEvent event) throws Exception {
        final Path worldPath = event.getServer().getEntityWorld().getSaveHandler().getWorldDirectory().toPath();

        final StorageFile storageFile = new StorageFile(worldPath);
        storageFile.initialize();

        final StorageDb storageDb = new StorageDb();
        storageDb.initialize();

        storage = new StorageFallback(storageFile, storageDb);
        storage.initialize();
    }


}
