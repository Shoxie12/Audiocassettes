package com.shoxie.audiocassettes.capability;

import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class WalkmanCapability implements ICapabilitySerializable<NBTTagCompound> {
	
	 IItemHandler h = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getDefaultInstance();

	@Nonnull
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return hasCapability(capability, facing) ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.<T>cast(h) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("walkman", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(h, null));
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(h, null, nbt.getTag("walkman"));
    }

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}
}