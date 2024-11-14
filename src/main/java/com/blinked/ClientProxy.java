package com.blinked;

import com.blinked.handler.Keybinds;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.io.File;

public class ClientProxy extends ServerProxy {

    @Override
    public File getDataDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public void init(FMLInitializationEvent e) {
        Keybinds.init();
        MinecraftForge.EVENT_BUS.register(new Keybinds());
    }

}
