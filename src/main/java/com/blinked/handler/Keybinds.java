package com.blinked.handler;

import com.blinked.BlinkedMain;
import com.blinked.capability.BlinkCapability;
import com.blinked.config.CommonConfig;
import com.blinked.packets.KeybindPacket;
import com.blinked.packets.PacketUpdateAlpha;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class Keybinds {
    public static KeyBinding KEY_BLINK = new KeyBinding("Blink", Keyboard.KEY_Y, "Blink");

    public static void init(){
        ClientRegistry.registerKeyBinding(KEY_BLINK);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        BlinkCapability.IEyeState props = BlinkCapability.getEyeState(BlinkedMain.proxy.me());

        for(EnumKeybind key : EnumKeybind.values()) {
            boolean last = props.getKeyPressed(key);
            boolean current = BlinkedMain.proxy.getIsKeyPressed(key);

            if(last != current) {
                BlinkedMain.wrapper.sendToServer(new KeybindPacket(key, current));
                props.setKeyPressed(key, current);
            }
        }

    }

    public static enum EnumKeybind {
        BLINK
    }
}
