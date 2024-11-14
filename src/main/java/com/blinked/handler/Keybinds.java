package com.blinked.handler;

import com.blinked.config.CommonConfig;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class Keybinds {
    public static KeyBinding KEY_BLINK;

    public static void init(){
        KEY_BLINK=new KeyBinding("Blink", Keyboard.KEY_Y, "Blink");
        ClientRegistry.registerKeyBinding(KEY_BLINK);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(KEY_BLINK.getKeyCode())) {
            if(ModEventHandler.canBlink){
                if (!ModEventHandler.isBlinking) {
                    ModEventHandler.startBlinking();
                }
            }
            ModEventHandler.isKeyHeld = true;
        } else {
            ModEventHandler.isKeyHeld = false;
        }
    }
}
