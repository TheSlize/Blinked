package com.blinked.packets;

import com.blinked.handler.ModEventHandler;
import com.blinked.handler.ModEventHandlerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateAlpha implements IMessage {
    private float alpha;

    public PacketUpdateAlpha() {}

    public PacketUpdateAlpha(float f) {
        this.alpha = f;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.alpha = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(alpha);
    }

    public static class Handler implements IMessageHandler<PacketUpdateAlpha, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateAlpha message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                ModEventHandlerClient.setAlpha(message.alpha);
            });
            return null;
        }
    }
}