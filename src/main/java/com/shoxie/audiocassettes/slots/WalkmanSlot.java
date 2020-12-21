package com.shoxie.audiocassettes.slots;

import javax.annotation.Nonnull;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.container.WalkmanContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.networking.CWalkmanStopPacket;
import com.shoxie.audiocassettes.networking.Networking;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class WalkmanSlot extends SlotItemHandler{
	private WalkmanContainer c;
	public WalkmanSlot(IItemHandler handler, int index, int xPosition, int yPosition, ItemStack mp , WalkmanContainer container) {
		super(handler, index, xPosition, yPosition);
		this.c = container;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
	    if(stack.getItem() instanceof AbstractAudioCassetteItem)
	    	return true;
	    return false;
	}
	
	@Override
	public void onSlotChanged() {
		if(c.player.world.isRemote())
			if(!this.getHasStack() && audiocassettes.proxy.isWalkmanPlaying(
					WalkmanItem.getID(WalkmanItem.getMPInHand(audiocassettes.proxy.getClientPlayer()))
					)) 
				Networking.INSTANCE.sendToServer(new CWalkmanStopPacket());
			else {
		        this.c.stitle = AbstractAudioCassetteItem.getSongTitle(this.getStack());
		        this.c.cursong = AbstractAudioCassetteItem.getCurrentSlot(this.getStack());
		        this.c.maxsongs = AbstractAudioCassetteItem.getMaxSlots(this.getStack());
			}
		    this.inventory.markDirty();
	}
}
