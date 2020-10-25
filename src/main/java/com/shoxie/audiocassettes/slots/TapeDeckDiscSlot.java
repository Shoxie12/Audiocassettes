package com.shoxie.audiocassettes.slots;

import com.shoxie.audiocassettes.tile.TileTapeDeck;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TapeDeckDiscSlot extends SlotItemHandler{
	TileTapeDeck tile;
	public TapeDeckDiscSlot(IItemHandler handler, int index, int xPosition, int yPosition, TileTapeDeck tile) {
		super(handler, index, xPosition, yPosition);
		this.tile = tile;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
	    if(stack.getItem() instanceof net.minecraft.item.ItemRecord)
	    	return true;
	    return false;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return !tile.isWriting();
	}
	
	@Override
	public void onSlotChanged() {
	    this.inventory.markDirty();
	}
}
