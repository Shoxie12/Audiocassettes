package com.shoxie.audiocassettes.container;

import com.shoxie.audiocassettes.slots.BoomBoxSlot;
import com.shoxie.audiocassettes.tile.TileBoomBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BoomBoxContainer extends Container {

    private TileBoomBox tile;
    public int curr = 0;
	public int max = 0;
	public String title = "--Empty--";
	
    public BoomBoxContainer(IInventory playerInventory, TileBoomBox tile) {
        this.tile = tile;

        IItemHandler itemHandler = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        addSlotToContainer(new BoomBoxSlot(itemHandler, 0, 51, 17, this));
        layoutPlayerInventorySlots(8, 65, playerInventory);
        }
    
    private int addSlotRange(IInventory handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
        	addSlotToContainer(new Slot(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IInventory handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow, IInventory playerInventory) {
    	
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    } 
    
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
	    return tile.canInteractWith(playerIn);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
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
	            if (!this.mergeItemStack(stack, 0, 2, false)) {
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

	public BlockPos getPos() {
		return this.tile.getPos();
	}
	
	public TileBoomBox getTile() {
		return this.tile;
	}
	
	public boolean isPlaying()
	{
		return this.tile.isPlaying;
	}
}