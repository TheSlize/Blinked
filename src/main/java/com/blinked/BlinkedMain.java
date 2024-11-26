package com.blinked;

import com.blinked.capability.BlinkCapability;
import com.blinked.config.CommonConfig;
import com.blinked.handler.CommandBlink;
import com.blinked.handler.Keybinds;
import com.blinked.handler.ModEventHandler;
import com.blinked.packets.KeybindPacket;
import com.blinked.packets.PacketUpdateAlpha;
import com.blinked.packets.PacketUpdateBlink;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
        modid = BlinkedMain.MOD_ID,
        name = BlinkedMain.MOD_NAME,
        version = BlinkedMain.VERSION,
        dependencies = "required-after:scp"
)
public class BlinkedMain {

    public static final String MOD_ID = "blink";
    public static final String MOD_NAME = "Blink";
    public static final String VERSION = "1.12.2-1.2";
    public static Logger logger;
    public static final SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("blinkChannel");

    @Mod.Instance(MOD_ID)
    public static BlinkedMain INSTANCE;

    @SidedProxy(clientSide = "com.blinked.ClientProxy", serverSide = "com.blinked.ServerProxy")
    public static ServerProxy proxy;
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
            if(logger == null)
                logger = event.getModLog();
        CapabilityManager.INSTANCE.register(BlinkCapability.IEyeState.class, new BlinkCapability.EyeStateStorage(), BlinkCapability.EyeState.FACTORY);
        MinecraftForge.EVENT_BUS.register(new ModEventHandler());
        reloadConfig();
        if(event.getSide() == Side.CLIENT) {
            Keybinds keyHandler = new Keybinds();
            MinecraftForge.EVENT_BUS.register(keyHandler);
        }
        wrapper.registerMessage(PacketUpdateBlink.Handler.class, PacketUpdateBlink.class, 1, Side.CLIENT);
        wrapper.registerMessage(KeybindPacket.Handler.class, KeybindPacket.class, 3, Side.SERVER);
        wrapper.registerMessage(KeybindPacket.Handler.class, KeybindPacket.class, 2, Side.CLIENT);
        wrapper.registerMessage(PacketUpdateAlpha.Handler.class, PacketUpdateAlpha.class, 4, Side.CLIENT);
        proxy.registerRenderInfo();
    }

    public static void reloadConfig() {
        File oldConfig = new File(proxy.getDataDir().getPath() + "/config/blinked.cfg");
        if(oldConfig.exists()){
            oldConfig.delete();
        }
        Configuration config = new Configuration(new File(proxy.getDataDir().getPath() + "/config/blink.cfg"));
        config.load();
        CommonConfig.loadFromConfig(config);
        config.save();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {}
    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        CommandBlink.register(event);
    }
}
