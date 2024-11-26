package com.blinked;

import com.blinked.handler.Keybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ServerProxy {
    public void registerRenderInfo() { }
    public void preInit(FMLPreInitializationEvent evt) {}

    public File getDataDir(){
        return FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory();
    }

    public void postInit(FMLPostInitializationEvent e){
    }
    public void init(FMLInitializationEvent e){}

    public EntityPlayer me() {
        return null;
    }

    public boolean getIsKeyPressed(Keybinds.EnumKeybind key) {
        return false;
    }
}
