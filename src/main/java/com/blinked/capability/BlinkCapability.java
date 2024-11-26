package com.blinked.capability;

import com.blinked.handler.Keybinds.EnumKeybind;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import java.util.concurrent.Callable;

public class BlinkCapability {

    public interface IEyeState {
        public int getBlinkCounter();
        public void setBlinkCounter(int i);
        public boolean hadManualStop();
        public void setManualStop(boolean b);
        public int getBlinkTimer();
        public void setBlinkTimer(int i);
        public boolean getKeyPressed(EnumKeybind key);
        public void setKeyPressed(EnumKeybind key, boolean pressed);
        boolean areEyesClosed();

        public void setEyesClosed(boolean closed);
    }
    public static class EyeState implements IEyeState {

        public static final Callable<IEyeState> FACTORY = EyeState::new;
        private boolean[] keysPressed = new boolean[EnumKeybind.values().length];
        private boolean eyesClosed = false;
        private boolean hadManualStop = true;
        private int blinkTimer = 0;
        private int blinkCounter = 0;

        @Override
        public int getBlinkCounter() {
            return blinkCounter;
        }
        @Override
        public void setBlinkCounter(int i) {
            this.blinkCounter = i;
        }
        @Override
        public boolean hadManualStop() {
            return hadManualStop;
        }
        @Override
        public void setManualStop(boolean b) {
            this.hadManualStop = b;
        }
        @Override
        public int getBlinkTimer() {
            return blinkTimer;
        }
        @Override
        public void setBlinkTimer(int i) {
            this.blinkTimer = i;
        }
        @Override
        public boolean getKeyPressed(EnumKeybind key) {
            return keysPressed[key.ordinal()];
        }

        @Override
        public void setKeyPressed(EnumKeybind key, boolean pressed) {
            if(!getKeyPressed(key) && pressed) {

                if(key == EnumKeybind.BLINK) {
                    this.eyesClosed = !this.eyesClosed;
                }
            }
            keysPressed[key.ordinal()] = pressed;
        }
        @Override
        public boolean areEyesClosed() {
            return eyesClosed;
        }

        @Override
        public void setEyesClosed(boolean closed) {
            this.eyesClosed = closed;
        }
    }

    public static class EyeStateStorage implements Capability.IStorage<IEyeState> {

        @Override
        public NBTBase writeNBT(Capability<IEyeState> capability, IEyeState instance, EnumFacing side) {
            NBTTagCompound tag = new NBTTagCompound();
            for(EnumKeybind key : EnumKeybind.values()){
                tag.setBoolean(key.name(), instance.getKeyPressed(key));
            }
            tag.setBoolean("eyesClosed", instance.areEyesClosed());
            tag.setInteger("blinkTimer", instance.getBlinkTimer());
            tag.setBoolean("hadManualStop", instance.hadManualStop());
            tag.setInteger("blinkCounter", instance.getBlinkCounter());
            return tag;
        }

        @Override
        public void readNBT(Capability<IEyeState> capability, IEyeState instance, EnumFacing side, NBTBase nbt) {
            if (nbt instanceof NBTTagCompound) {
                NBTTagCompound tag = (NBTTagCompound) nbt;
                for(EnumKeybind key : EnumKeybind.values()){
                    instance.setKeyPressed(key, tag.getBoolean(key.name()));
                }
                instance.setEyesClosed(tag.getBoolean("eyesClosed"));
                instance.setBlinkTimer(tag.getInteger("blinkTimer"));
                instance.setManualStop(tag.getBoolean("hadManualStop"));
                instance.setBlinkCounter(tag.getInteger("blinkCounter"));
            }
        }
    }

    public static class EyeStateProvider implements ICapabilitySerializable<NBTBase> {

        public static final IEyeState DUMMY = new IEyeState(){
            @Override
            public int getBlinkCounter() {
                return 0;
            }
            @Override
            public void setBlinkCounter(int i) {}
            @Override
            public boolean hadManualStop() {
                return true;
            }
            @Override
            public void setManualStop(boolean b) {}
            @Override
            public int getBlinkTimer() {
                return 0;
            }
            @Override
            public void setBlinkTimer(int i) {}

            @Override
            public boolean getKeyPressed(EnumKeybind key) {
                return false;
            }

            @Override
            public void setKeyPressed(EnumKeybind key, boolean pressed) {
            }

            public boolean areEyesClosed(){ return false; }

            public void setEyesClosed(boolean closed){}
        };

        @CapabilityInject(IEyeState.class)
        public static final Capability<IEyeState> EYE_STATE_CAPABILITY = null;

        private IEyeState instance = EYE_STATE_CAPABILITY.getDefaultInstance();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == EYE_STATE_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == EYE_STATE_CAPABILITY ? EYE_STATE_CAPABILITY.cast(this.instance) : null;
        }

        @Override
        public NBTBase serializeNBT() {
            return EYE_STATE_CAPABILITY.getStorage().writeNBT(EYE_STATE_CAPABILITY, instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            EYE_STATE_CAPABILITY.getStorage().readNBT(EYE_STATE_CAPABILITY, instance, null, nbt);
        }
    }

    public static IEyeState getEyeState(Entity entity) {
        if (entity.hasCapability(EyeStateProvider.EYE_STATE_CAPABILITY, null)) {
            return entity.getCapability(EyeStateProvider.EYE_STATE_CAPABILITY, null);
        }
        return EyeStateProvider.DUMMY;
    }
}
