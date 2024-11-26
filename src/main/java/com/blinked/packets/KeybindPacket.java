package com.blinked.packets;

import com.blinked.BlinkedMain;
import com.blinked.capability.BlinkCapability;
import com.blinked.handler.Keybinds.EnumKeybind;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class KeybindPacket implements IMessage {

    int id;
    int key;
    boolean pressed;

    public KeybindPacket() { }

    public KeybindPacket(EnumKeybind key, boolean pressed) {
        this.key = key.ordinal();
        this.pressed = pressed;
        this.id = 0;
    }

    public KeybindPacket(int id) {
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        key = buf.readInt();
        pressed = buf.readBoolean();
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(key);
        buf.writeBoolean(pressed);
        buf.writeInt(id);
    }

    public static class Handler implements IMessageHandler<KeybindPacket, IMessage> {

        @Override
        public IMessage onMessage(KeybindPacket m, MessageContext ctx) {
            if(ctx.side == Side.SERVER){
                ctx.getServerHandler().player.getServer().addScheduledTask(() -> {
                    switch(m.id){
                        case 0:
                            EntityPlayer p = ctx.getServerHandler().player;
                            BlinkCapability.IEyeState props = BlinkCapability.getEyeState(p);

                            props.setKeyPressed(EnumKeybind.values()[m.key], m.pressed);
                            break;
                    }
                });
            } else {
                handleClient(ctx, m);
            }
            return null;
        }

        @SideOnly(Side.CLIENT)
        public void handleClient(MessageContext ctx, KeybindPacket m){
            Minecraft.getMinecraft().addScheduledTask(() -> {
                BlinkCapability.IEyeState props = BlinkCapability.getEyeState(Minecraft.getMinecraft().player);
                if(EnumKeybind.values()[m.key] == EnumKeybind.BLINK) {
                    props.setEyesClosed(m.pressed);
                }
            });
        }
    }
}
