package com.blinked.capability;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import java.util.concurrent.Callable;

public class BlinkCapability {

    public interface IEyeState {
        boolean areEyesClosed();

        void setEyesClosed(boolean closed);
    }
    public static class EyeState implements IEyeState {

        public static final Callable<IEyeState> FACTORY = () -> {return new EyeState();};
        private boolean eyesClosed = false;

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
            tag.setBoolean("eyesClosed", instance.areEyesClosed());
            return tag;
        }

        @Override
        public void readNBT(Capability<IEyeState> capability, IEyeState instance, EnumFacing side, NBTBase nbt) {
            if (nbt instanceof NBTTagCompound) {
                NBTTagCompound tag = (NBTTagCompound) nbt;
                instance.setEyesClosed(tag.getBoolean("eyesClosed"));
            }
        }
    }

    public static class EyeStateProvider implements ICapabilitySerializable<NBTBase> {

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
        return null;
    }
}
