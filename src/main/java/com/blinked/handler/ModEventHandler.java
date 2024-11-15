package com.blinked.handler;

import alexiy.secure.contain.protect.Utils;
import alexiy.secure.contain.protect.entity.EntitySculpture;
import alexiy.secure.contain.protect.registration.Sounds;
import com.blinked.BlinkedMain;
import com.blinked.capability.BlinkCapability;
import com.blinked.config.CommonConfig;
import com.blinked.packets.PacketUpdateBlink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

public class ModEventHandler {
    public static boolean serverCanBlink = true;

    public static void setServerBlinking(boolean state) {
        serverCanBlink = state;
        canBlink = state;
        BlinkedMain.wrapper.sendToAll(new PacketUpdateBlink(state));
    }
    public static boolean canBlink = true;
    public static int bufferTimer;
    public static boolean isBlinking = false;
    private static int blinkStage = 0;
    private static  int blinkTickCounter = 0;
    public static boolean isKeyHeld = false;
    public static boolean areEyesClosed = false;
    private static boolean hasTeleportedAndKilled = false;

    public static void setRandomBufferTimer() {
        Random random = new Random();
        if(CommonConfig.maxBlinkTimer > CommonConfig.minBlinkTimer){
            bufferTimer = CommonConfig.minBlinkTimer + random.nextInt(CommonConfig.maxBlinkTimer - CommonConfig.minBlinkTimer);
        } else if(CommonConfig.maxBlinkTimer == CommonConfig.minBlinkTimer) {
            bufferTimer = CommonConfig.minBlinkTimer;
        } else {
            bufferTimer = CommonConfig.maxBlinkTimer + random.nextInt(CommonConfig.minBlinkTimer - CommonConfig.maxBlinkTimer);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (canBlink) {
                if (!isBlinking) {
                    bufferTimer--;
                    if (bufferTimer <= 0) {
                        startBlinking();
                    }
                } else {
                    if(!hasTeleportedAndKilled) handleBlinking();
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote && event.phase == TickEvent.Phase.END) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            if (areEyesClosed && !hasTeleportedAndKilled) {
                BlinkCapability.IEyeState props = BlinkCapability.getEyeState(event.player);
                props.setEyesClosed(true);
                if (teleportSculptureAndKillPlayer(player)) {
                    stopBlinking();
                    props.setEyesClosed(false);
                }
            }
        }
    }

    public static void startBlinking() {
        isBlinking = true;
        blinkStage = 1;
        blinkTickCounter = 0;
    }

    private void handleBlinking() {
        switch (blinkStage) {
            case 1:
                areEyesClosed = false;
                if(CommonConfig.closeEyeDuration == 0) {
                    blinkStage = 2;
                    blinkTickCounter = 0;
                    applyBlindness();
                }
                blinkTickCounter++;
                if (blinkTickCounter >= CommonConfig.closeEyeDuration) {
                    blinkStage = 2;
                    blinkTickCounter = 0;
                    applyBlindness();
                }
                break;
            case 2:
                areEyesClosed = true;
                if (isKeyHeld) {
                    applyBlindness();
                } else {
                    blinkTickCounter++;
                    if (blinkTickCounter >= CommonConfig.blackScreenDuration) {
                        blinkStage = 3;
                        blinkTickCounter = 0;
                    }
                }
                break;
            case 3:
                areEyesClosed = false;
                if(CommonConfig.openEyeDuration == 0) {
                    stopBlinking();
                }
                blinkTickCounter++;
                if (blinkTickCounter >= CommonConfig.openEyeDuration) {
                    stopBlinking();
                }
                break;
        }
    }

    private void applyBlindness() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            for (WorldServer world : server.worlds) {
                for (EntityPlayer player : world.playerEntities) {
                    player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, CommonConfig.blackScreenDuration, 1, false, false));
                }
            }
        }
    }

    private void stopBlinking() {
        isBlinking = false;
        blinkStage = 0;
        setRandomBufferTimer();
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if (isBlinking) {
                float alpha = 0.0f;
                switch (blinkStage) {
                    case 1:
                        alpha = interpolate(blinkTickCounter, CommonConfig.closeEyeDuration);
                        break;
                    case 2:
                        alpha = 1.0f;
                        break;
                    case 3:
                        alpha = 1.0f - interpolate(blinkTickCounter, CommonConfig.openEyeDuration);
                        break;
                }

                if (alpha > 0.0f) {
                    drawBlackOverlay(alpha);
                }
            }
        }
    }

    private float interpolate(int tick, int maxTicks) {
        float progress = (float) tick / maxTicks;
        return (float) (1.0f / (1.0f + Math.exp(-10.0f * (progress - 0.5f))));
    }

    private void drawBlackOverlay(float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        int width = mc.displayWidth;
        int height = mc.displayHeight;
        Gui.drawRect(0, 0, width, height, (int) (alpha * 255) << 24);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        stopBlinking();
        blinkTickCounter = 0;
        BlinkCapability.IEyeState props = BlinkCapability.getEyeState(event.player);
        props.setEyesClosed(false);
        hasTeleportedAndKilled = false;
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation("blink", "eye_state"), new BlinkCapability.EyeStateProvider());
        }
    }

    private boolean teleportSculptureAndKillPlayer(EntityPlayerMP player) {
        try {
            Field invulnerabilityField = EntityPlayerMP.class.getDeclaredField("respawnInvulnerabilityTicks");
            invulnerabilityField.setAccessible(true);
            int invulnerabilityTicks = invulnerabilityField.getInt(player);
            if(player.capabilities.isCreativeMode || player.hurtResistantTime > 0 || player.isInvulnerableDimensionChange() || invulnerabilityTicks > 0){
                return false;
            }
        World world = player.world;
        AxisAlignedBB searchBox = new AxisAlignedBB(
                player.posX - 6, player.posY, player.posZ - 6,
                player.posX + 6, player.posY + 1, player.posZ + 6
        );
        AxisAlignedBB searchBoxFar = new AxisAlignedBB(
                player.posX - 64, player.posY, player.posZ - 64,
                player.posX + 64, player.posY + 1, player.posZ + 64
        );

        List<EntitySculpture> sculptures = world.getEntitiesWithinAABB(EntitySculpture.class, searchBox);
        List<EntityPlayerMP> players = world.getEntitiesWithinAABB(EntityPlayerMP.class, searchBoxFar);
        players.remove(player);

        if (!sculptures.isEmpty()) {
            EntitySculpture sculpture = sculptures.get(0);
            if(!players.isEmpty()) {
                for(EntityPlayerMP plr: players){
                    BlinkCapability.IEyeState eyeState = BlinkCapability.getEyeState(plr);
                    if (eyeState != null && !eyeState.areEyesClosed() && Utils.isInSightOf(sculpture, plr, 80.0F)) {
                        return false;
                    }
                }
            }
                sculpture.setPosition(player.posX, player.posY, player.posZ);
                sculpture.playSound(Sounds.sculpture_neck_snap, 1.0F, 1.0F);
                player.attackEntityFrom(DamageSource.causeMobDamage(sculpture), 10000.F);
                hasTeleportedAndKilled = true;
                areEyesClosed = false;
                return true;
        }
        return false;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
