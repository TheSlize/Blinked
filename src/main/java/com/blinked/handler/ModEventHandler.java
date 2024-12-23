package com.blinked.handler;

import alexiy.secure.contain.protect.Utils;
import alexiy.secure.contain.protect.entity.EntitySculpture;
import alexiy.secure.contain.protect.registration.Sounds;
import com.blinked.BlinkedMain;
import com.blinked.capability.BlinkCapability;
import com.blinked.config.CommonConfig;
import com.blinked.packets.PacketUpdateAlpha;
import com.blinked.packets.PacketUpdateBlink;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Optional;
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
    public static EntityPlayer player;
    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation("blink", "eye_state"), new BlinkCapability.EyeStateProvider());
        }
    }

    public static void setPlr(EntityPlayer plr) {
        player = plr;
    }

    public static int setRandomBufferTimer() {
        Random random = new Random();
        if(CommonConfig.maxBlinkTimer > CommonConfig.minBlinkTimer){
            return CommonConfig.minBlinkTimer + random.nextInt(CommonConfig.maxBlinkTimer - CommonConfig.minBlinkTimer);
        } else if(CommonConfig.maxBlinkTimer == CommonConfig.minBlinkTimer) {
            return CommonConfig.minBlinkTimer;
        } else {
            return CommonConfig.maxBlinkTimer + random.nextInt(CommonConfig.minBlinkTimer - CommonConfig.maxBlinkTimer);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote && event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            BlinkCapability.IEyeState props = BlinkCapability.getEyeState(event.player);
            setPlr(player);
            if (canBlink) {
                if(props.getKeyPressed(Keybinds.EnumKeybind.BLINK)) {
                    props.setManualStop(false);
                    handleBlinking(props, player);
                } else if(!props.hadManualStop()){
                    stopBlinking(props);
                    props.setManualStop(true);
                }
                if (!props.areEyesClosed()) {
                    props.setBlinkTimer(props.getBlinkTimer() - 1);
                    if (props.getBlinkTimer() <= 0) {
                        handleBlinking(props, player);
                    }
                } else {
                    if (props.getBlinkTimer() <= 0) {
                        handleBlinking(props, player);
                    }
                    if(Loader.isModLoaded("scp")) {
                        if (teleportSculptureAndKillPlayer(player)) {
                            stopBlinking(props);
                        }
                    }
                }
            }
        }
    }
    private void handleBlinking(BlinkCapability.IEyeState props, EntityPlayer player) {
        try {
            BlinkedMain.wrapper.sendTo(new PacketUpdateAlpha(1.0F), (EntityPlayerMP) player);
            if (props.getKeyPressed(Keybinds.EnumKeybind.BLINK)) {
                applyBlindness(player);
                if (!props.areEyesClosed()) {
                    props.setEyesClosed(true);
                }
            } else {
                if (!props.areEyesClosed()) {
                    props.setEyesClosed(true);
                }
                props.setBlinkCounter(props.getBlinkCounter() + 1);
                applyBlindness(player);
                if (props.getBlinkCounter() >= CommonConfig.blackScreenDuration) {
                    stopBlinking(props);
                }
            }
        } catch(NullPointerException e) {}
    }

    private void applyBlindness(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 1, 1, false, false));
    }

    private void stopBlinking(BlinkCapability.IEyeState props) {
        BlinkedMain.wrapper.sendTo(new PacketUpdateAlpha(0.0F), (EntityPlayerMP) player);
        props.setEyesClosed(false);
        props.setBlinkCounter(0);
        props.setBlinkTimer(setRandomBufferTimer());
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        BlinkCapability.IEyeState props = BlinkCapability.getEyeState(event.player);
        BlinkedMain.wrapper.sendTo(new PacketUpdateAlpha(0.0F), (EntityPlayerMP) event.player);
        props.setBlinkCounter(0);
        props.setBlinkTimer(setRandomBufferTimer());
        props.setEyesClosed(false);
    }
    @Optional.Method(modid = "scp")
    private boolean teleportSculptureAndKillPlayer(EntityPlayer player) {
            try {
                if (FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
                    EntityPlayerMP plrMP = (EntityPlayerMP) player;
                    Field invulnerabilityField = ObfuscationReflectionHelper.findField(EntityPlayerMP.class, "field_147101_bU");
                    invulnerabilityField.setAccessible(true);
                    int invulnerabilityTicks = invulnerabilityField.getInt(plrMP);
                    if (plrMP.capabilities.isCreativeMode || plrMP.hurtResistantTime > 0 || plrMP.isInvulnerableDimensionChange() || invulnerabilityTicks > 0) {
                        return false;
                    }
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
                List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayerMP.class, searchBoxFar);
                players.remove(player);

                if (!sculptures.isEmpty()) {
                    EntitySculpture sculpture = sculptures.get(0);
                    if (!players.isEmpty()) {
                        for (EntityPlayer plr : players) {
                            BlinkCapability.IEyeState eyeState = BlinkCapability.getEyeState(plr);
                            if (!plr.isDead && eyeState != null && !eyeState.areEyesClosed() && Utils.isInSightOf(sculpture, plr, 80.0F)) {
                                return false;
                            }
                        }
                    }
                    if (player.isDead) return false;
                    Vec3d playerPos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                    Vec3d sculpturePos = new Vec3d(sculpture.posX, sculpture.posY + sculpture.getEyeHeight(), sculpture.posZ);
                    RayTraceResult result = world.rayTraceBlocks(playerPos, sculpturePos, false, true, false);

                    if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                        return false;
                    }
                    sculpture.setPosition(player.posX, player.posY, player.posZ);
                    sculpture.playSound(Sounds.sculpture_neck_snap, 1.0F, 1.0F);
                    player.attackEntityFrom(DamageSource.causeMobDamage(sculpture), 10000.F);
                    return true;
                }
                return false;

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
    }
}
