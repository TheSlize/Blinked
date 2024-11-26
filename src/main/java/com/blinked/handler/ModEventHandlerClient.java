package com.blinked.handler;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEventHandlerClient {

    private static float alpha;

    public static void setAlpha(float f){
        alpha = f;
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            drawBlackOverlay(alpha);
        }
    }

    private void drawBlackOverlay(float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        int width = mc.displayWidth;
        int height = mc.displayHeight;
        Gui.drawRect(0, 0, width, height, (int) (alpha * 255) << 24);
    }
}
