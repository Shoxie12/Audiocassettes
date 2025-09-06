package com.shoxie.audiocassettes.slots;

import com.shoxie.audiocassettes.entity.TapeDeckEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TapeDeckDiscSlot extends SlotItemHandler{
	TapeDeckEntity entity;
	public TapeDeckDiscSlot(IItemHandler handler, int index, int xPosition, int yPosition, TapeDeckEntity entity) {
		super(handler, index, xPosition, yPosition);
		this.entity = entity;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
	    if(stack.getItem() instanceof RecordItem)
	    	return true;
	    return false;
	}
	
	@Override
	public void setChanged() {
	    super.setChanged();
	}
}
