package com.shoxie.audiocassettes.slots;

import javax.annotation.Nonnull;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.menu.WalkmanMenu;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.networking.CWalkmanStopPacket;
import com.shoxie.audiocassettes.networking.Networking;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class WalkmanSlot extends SlotItemHandler{
	private final WalkmanMenu c;
	public WalkmanSlot(IItemHandler handler, int index, int xPosition, int yPosition, ItemStack mp , WalkmanMenu container) {
		super(handler, index, xPosition, yPosition);
		this.c = container;
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof AbstractAudioCassetteItem;
    }
	
	@Override
	public void setChanged() {
		if(!c.player.level().isClientSide()) {
            if (this.getItem().isEmpty() && audiocassettes.proxy.isWalkmanPlaying(
                    WalkmanItem.getID(WalkmanItem.getMPInHand(audiocassettes.proxy.getClientPlayer()))
            )) {
                Networking.INSTANCE.sendToServer(new CWalkmanStopPacket());
            }
            super.setChanged();
        }
	}
}
