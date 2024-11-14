package com.blinked.handler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommandBlink extends CommandBase {

    @Override
    public String getName() {
        return "blink";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/blink";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(2, this.getName());
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (ModEventHandler.serverCanBlink) {
            ModEventHandler.setServerBlinking(false);
            sender.sendMessage(new TextComponentString("Blinking mechanic turned off!"));
        } else {
            ModEventHandler.setServerBlinking(true);
            sender.sendMessage(new TextComponentString("Blinking mechanic turned on!"));
        }
    }

    public static void register(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandBlink());
    }
}
