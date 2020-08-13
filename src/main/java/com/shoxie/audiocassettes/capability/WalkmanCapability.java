package com.shoxie.audiocassettes.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class WalkmanCapability implements ICapabilitySerializable<CompoundNBT> {
	
    private final ItemStackHandler h = new ItemStackHandler(1);
    private final LazyOptional<IItemHandler> whandler = LazyOptional.of(() -> h);

	@Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return whandler.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("walkman", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(h, null));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(h, null, nbt.get("walkman"));
    }
}