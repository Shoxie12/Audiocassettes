package com.shoxie.audiocassettes.slots;

import javax.annotation.Nonnull;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.container.BoomBoxContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.networking.CBoomBoxStopPacket;
import com.shoxie.audiocassettes.networking.Networking;
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
		if(c.getTile().getWorld().isRemote())
			if(!this.getHasStack() && audiocassettes.proxy.isBoomBoxPlaying(c.getTile().getID())) {
					Networking.INSTANCE.sendToServer(new CBoomBoxStopPacket(c.getPos()));
					this.inventory.markDirty();
					return;
			}
			else if(this.getHasStack()){
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
