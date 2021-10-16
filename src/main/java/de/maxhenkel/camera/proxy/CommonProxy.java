package de.maxhenkel.camera.proxy;

import de.maxhenkel.camera.IStorage;
import de.maxhenkel.camera.Main;
import de.maxhenkel.camera.Registry;
import de.maxhenkel.camera.ServerEvents;
import de.maxhenkel.camera.StorageDb;
import de.maxhenkel.camera.StorageFallback;
import de.maxhenkel.camera.StorageFile;
import de.maxhenkel.camera.entities.EntityImage;
import de.maxhenkel.camera.gui.GuiHandler;
import de.maxhenkel.camera.net.MessageDisableCameraMode;
import de.maxhenkel.camera.net.MessageImage;
import de.maxhenkel.camera.net.MessageImageUnavailable;
import de.maxhenkel.camera.net.MessagePartialImage;
import de.maxhenkel.camera.net.MessageRequestImage;
import de.maxhenkel.camera.net.MessageResizeFrame;
import de.maxhenkel.camera.net.MessageSetShader;
import de.maxhenkel.camera.net.MessageTakeImage;
import de.maxhenkel.camera.net.PacketManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.nio.file.Path;
import java.text.SimpleDateFormat;

public class CommonProxy {

    public static SimpleNetworkWrapper simpleNetworkWrapper;

    public static PacketManager packetManager;

    public static SimpleDateFormat imageDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    public static int imageCooldown = 5000;
    public static String connectionUrl = null;
    public static String dbUser = null;
    public static String dbPassword = null;
    public static IStorage storage = null;

    public void preinit(FMLPreInitializationEvent event) {
        try {
            Configuration config = new Configuration(event.getSuggestedConfigurationFile());
            imageDateFormat = new SimpleDateFormat(config.getString("image_date_format", "camera", "MM/dd/yyyy HH:mm", "The format the date will be displayed on the image"));
            imageCooldown = config.get("image_cooldown", "camera", 5000, "The time in milliseconds the camera will be on cooldown after taking an image").getInt();
            connectionUrl = config.getString("tortilla_camera:jdbc_url", "camera", "FIX ME tortilla_camera:jdbc_url", "Use jdbc:mariadb://localhost:3306/camera_storage");
            dbUser = config.getString("tortilla_camera:db_user", "camera", "root", "user");
            dbPassword = config.getString("tortilla_camera:db_pass", "camera", "admin", "pass");
            config.save();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Main.MODID);
        packetManager = new PacketManager();
        simpleNetworkWrapper.registerMessage(MessageDisableCameraMode.class, MessageDisableCameraMode.class, 0, Side.SERVER);
        simpleNetworkWrapper.registerMessage(MessageImage.class, MessageImage.class, 1, Side.CLIENT);
        simpleNetworkWrapper.registerMessage(MessageImageUnavailable.class, MessageImageUnavailable.class, 2, Side.CLIENT);
        simpleNetworkWrapper.registerMessage(MessagePartialImage.class, MessagePartialImage.class, 3, Side.SERVER);
        simpleNetworkWrapper.registerMessage(MessageRequestImage.class, MessageRequestImage.class, 4, Side.SERVER);
        simpleNetworkWrapper.registerMessage(MessageResizeFrame.class, MessageResizeFrame.class, 5, Side.SERVER);
        simpleNetworkWrapper.registerMessage(MessageSetShader.class, MessageSetShader.class, 6, Side.SERVER);
        simpleNetworkWrapper.registerMessage(MessageTakeImage.class, MessageTakeImage.class, 7, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(new Registry());
        MinecraftForge.EVENT_BUS.register(new ServerEvents());
    }

    public void init(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "image_frame"), EntityImage.class,
                "corpse", 3635, Main.instance(), 256, 20, false);


        NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance(), new GuiHandler());
    }

    public void postinit(FMLPostInitializationEvent event) {

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
