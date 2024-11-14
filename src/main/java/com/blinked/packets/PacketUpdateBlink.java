package com.blinked.packets;

import com.blinked.handler.ModEventHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateBlink implements IMessage {
    private boolean canBlink;

    public PacketUpdateBlink() {}

    public PacketUpdateBlink(boolean canBlink) {
        this.canBlink = canBlink;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.canBlink = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(canBlink);
    }

    public static class Handler implements IMessageHandler<PacketUpdateBlink, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateBlink message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                ModEventHandler.canBlink = message.canBlink;
                ModEventHandler.serverCanBlink = message.canBlink;
            });
            return null;
        }
    }
}
