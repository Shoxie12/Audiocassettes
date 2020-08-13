package com.shoxie.audiocassettes.container;

import com.shoxie.audiocassettes.ModContainers;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.slots.WalkmanSlot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class WalkmanContainer extends Container {

    private IItemHandler playerInventory;
    public int cursong = 1;
	public int maxsongs = 0;
	public String stitle = "-";
	
    public WalkmanContainer(int windowId, PlayerInventory playerInventory, PlayerEntity playerentity) {
        super(ModContainers.CONTAINER_WALKMAN, windowId);
        this.playerInventory = new InvWrapper(playerInventory);
        ItemStack mp = WalkmanItem.getMPInHand(playerentity);
        mp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
			this.addSlot(new WalkmanSlot(h, 0, 50, 17,mp, this));
		});

        layoutPlayerInventorySlots(8, 65);
        }
    
    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
    	
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
    
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
	    ItemStack itemstack = ItemStack.EMPTY;
	    Slot slot = this.inventorySlots.get(index);
	    if (slot != null && slot.getHasStack()) {
	        ItemStack stack = slot.getStack();
	        itemstack = stack.copy();
	        if (index == 0) {
	            if (!this.mergeItemStack(stack, 1, 37, true)) {
	                return ItemStack.EMPTY;
	            }
	            slot.onSlotChange(stack, itemstack);
	        } else {
	            if (!this.mergeItemStack(stack, 0, 1, false)) {
				    return ItemStack.EMPTY;
				}
	        }

	        if (stack.isEmpty()) {
	            slot.putStack(ItemStack.EMPTY);
	        } else {
	            slot.onSlotChanged();
	        }

	        if (stack.getCount() == itemstack.getCount()) {
	            return ItemStack.EMPTY;
	        }

	        slot.onTake(playerIn, stack);
	    }

	    return itemstack;
	}
	
}
