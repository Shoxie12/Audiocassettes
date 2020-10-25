package com.shoxie.audiocassettes.slots;

import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.tile.TileTapeDeck;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TapeDeckCassetteSlot extends SlotItemHandler{
	TileTapeDeck tile;
	public TapeDeckCassetteSlot(IItemHandler handler, int index, int xPosition, int yPosition, TileTapeDeck tile) {
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
	public boolean canTakeStack(EntityPlayer playerIn) {
		return !tile.isWriting();
	}
	
	@Override
	public void onSlotChanged() {
		if(this.getHasStack())
			if(this.getStack().getItem() instanceof AbstractAudioCassetteItem)
				if(!this.getStack().hasTagCompound()) {
			        NBTTagCompound nbt = new NBTTagCompound();
			        ItemStack stack = this.getStack();
					for(int i=1;i<=AbstractAudioCassetteItem.getMaxSlots(stack);i++)
					{
						nbt.setString("Song"+i, ("audiocassettes"+":"+"empty"));
						nbt.setString("SongName"+i, "--Empty--");
					}
					nbt.setInteger("ms", 1);
					stack.setTagCompound(nbt);
				}
		
		//tile.cancelWrite();
	    this.inventory.markDirty();
	}
}
