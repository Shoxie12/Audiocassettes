package com.shoxie.audiocassettes.slots;

import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.tile.TapeDeckTile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TapeDeckCassetteSlot extends SlotItemHandler{
	TapeDeckTile tile;
	public TapeDeckCassetteSlot(IItemHandler handler, int index, int xPosition, int yPosition, TapeDeckTile tile) {
		super(handler, index, xPosition, yPosition);
		this.tile = tile;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
	    if(stack.getItem() instanceof AbstractAudioCassetteItem)
	    	return true;
	    return false;
	}
	
	@Override
	public void onSlotChanged() {
		if(this.getHasStack())
			if(this.getStack().getItem() instanceof AbstractAudioCassetteItem)
				if(!this.getStack().hasTag()) {
			        CompoundNBT nbt = new CompoundNBT();
			        ItemStack stack = this.getStack();
					for(int i=1;i<=AbstractAudioCassetteItem.getMaxSlots(stack);i++)
					{
						nbt.putString("Song"+i, ("audiocassettes"+":"+"empty"));
						nbt.putString("SongName"+i, "--Empty--");
					}
					nbt.putInt("ms", 1);
					stack.setTag(nbt);
				}
		
		tile.cancelWrite();
	    this.inventory.markDirty();
	}
}
