package com.shoxie.audiocassettes.slots;

import javax.annotation.Nonnull;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.container.BoomBoxContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.networking.CBoomBoxStopPacket;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.tile.BoomBoxTile;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BoomBoxSlot extends SlotItemHandler{
	private BoomBoxContainer c;
	public BoomBoxSlot(IItemHandler handler, int index, int xPosition, int yPosition, BoomBoxContainer c) {
		super(handler, index, xPosition, yPosition);
		this.c = c;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
	    if(stack.getItem() instanceof AbstractAudioCassetteItem)
	    	return true;
	    return false;
	}
	
	@Override
	public void onSlotChanged() {   
		BoomBoxTile tile = (BoomBoxTile) audiocassettes.proxy.getClientPlayer().world.getTileEntity(c.getPos());
		
		if(!this.getHasStack() && audiocassettes.proxy.isBoomBoxPlaying(tile.getID()))
			Networking.INSTANCE.sendToServer(new CBoomBoxStopPacket(c.getPos()));
		else {
	    	ItemStack stack = this.getStack();
	    	if(stack.getItem() instanceof AbstractAudioCassetteItem) {
	    		c.max = AbstractAudioCassetteItem.getMaxSlots(stack);
	    		c.curr = AbstractAudioCassetteItem.getCurrentSlot(stack);
	    		c.title = AbstractAudioCassetteItem.getSongTitle(stack);
    		}
    	}
		
	    this.inventory.markDirty();
	}
}
