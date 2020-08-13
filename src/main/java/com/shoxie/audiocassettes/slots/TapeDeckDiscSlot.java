package com.shoxie.audiocassettes.slots;

import com.shoxie.audiocassettes.tile.TapeDeckTile;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TapeDeckDiscSlot extends SlotItemHandler{
	TapeDeckTile tile;
	public TapeDeckDiscSlot(IItemHandler handler, int index, int xPosition, int yPosition, TapeDeckTile tile) {
		super(handler, index, xPosition, yPosition);
		this.tile = tile;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
	    if(stack.getItem() instanceof net.minecraft.item.MusicDiscItem)
	    	return true;
	    return false;
	}
	
	@Override
	public void onSlotChanged() {
		tile.cancelWrite();
	    this.inventory.markDirty();
	}
}
