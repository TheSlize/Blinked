package com.blinked;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ServerProxy {
    public void preInit(FMLPreInitializationEvent evt) {}

    public File getDataDir(){
        return FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory();
    }

    public void postInit(FMLPostInitializationEvent e){
    }
    public void init(FMLInitializationEvent e){}
}
