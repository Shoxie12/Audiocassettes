package com.shoxie.audiocassettes.slots;

import javax.annotation.Nonnull;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.menu.BoomBoxMenu;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.networking.CBoomBoxStopPacket;
import com.shoxie.audiocassettes.networking.Networking;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BoomBoxSlot extends SlotItemHandler{
	private final BoomBoxMenu c;
	public BoomBoxSlot(IItemHandler handler, int index, int xPosition, int yPosition, BoomBoxMenu c) {
		super(handler, index, xPosition, yPosition);
		this.c = c;
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
	    if(stack.getItem() instanceof AbstractAudioCassetteItem)
	    	return true;
	    return false;
	}
	
	@Override
	public void setChanged() {
		if(!c.getEntity().getLevel().isClientSide())
			if(this.getItem().isEmpty() && audiocassettes.proxy.isBoomBoxPlaying(c.getEntity().getID())) {
					Networking.INSTANCE.sendToServer(new CBoomBoxStopPacket(c.getPos()));
                    super.setChanged();
					return;
			}
	    super.setChanged();
	}
}
