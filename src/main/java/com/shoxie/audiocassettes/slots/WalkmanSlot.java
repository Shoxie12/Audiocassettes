package com.shoxie.audiocassettes.slots;

import javax.annotation.Nonnull;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.container.WalkmanContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.networking.CWalkmanStopPacket;
import com.shoxie.audiocassettes.networking.Networking;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class WalkmanSlot extends SlotItemHandler{
	private WalkmanContainer container;
	public WalkmanSlot(IItemHandler handler, int index, int xPosition, int yPosition, WalkmanContainer container) {
		super(handler, index, xPosition, yPosition);
		this.container = container;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
	    if(stack.getItem() instanceof AbstractAudioCassetteItem)
	    	return true;
	    return false;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return !audiocassettes.proxy.isWalkmanPlaying(WalkmanItem.getID(WalkmanItem.getMPInHand(audiocassettes.proxy.getClientPlayer())));
	}
	
	@Override
	public void onSlotChanged() {        
		if(!this.getHasStack() && audiocassettes.proxy.isWalkmanPlaying(WalkmanItem.getID(WalkmanItem.getMPInHand(audiocassettes.proxy.getClientPlayer())))) {
			Networking.INSTANCE.sendToServer(new CWalkmanStopPacket());
			return;
		}
		else {
	        this.container.stitle = AbstractAudioCassetteItem.getSongTitle(this.getStack());
	        this.container.cursong = AbstractAudioCassetteItem.getCurrentSlot(this.getStack());
	        this.container.maxsongs = AbstractAudioCassetteItem.getMaxSlots(this.getStack());
		}
	    this.inventory.markDirty();
	}
}
