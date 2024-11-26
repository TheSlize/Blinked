package com.blinked;

import com.blinked.handler.Keybinds;
import com.blinked.handler.ModEventHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.io.File;

public class ClientProxy extends ServerProxy {

    @Override
    public File getDataDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public void init(FMLInitializationEvent e) {
    }

    @Override
    public EntityPlayer me() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public void registerRenderInfo()
    {
        MinecraftForge.EVENT_BUS.register(new ModEventHandlerClient());
        Keybinds.init();
    }

    @Override
    public boolean getIsKeyPressed(Keybinds.EnumKeybind key) {
        switch(key){
            case BLINK:	return Keybinds.KEY_BLINK.isKeyDown();
        }
        return false;
    }

}
