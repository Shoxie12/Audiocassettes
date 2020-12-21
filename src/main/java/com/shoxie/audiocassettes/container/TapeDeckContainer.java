package com.shoxie.audiocassettes.container;

import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.slots.TapeDeckCassetteSlot;
import com.shoxie.audiocassettes.slots.TapeDeckDiscSlot;
import com.shoxie.audiocassettes.tile.TileTapeDeck;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TapeDeckContainer extends Container {

    private TileTapeDeck tileEntity;
    public TapeDeckContainer(InventoryPlayer playerInventory, TileTapeDeck tile) {
    	this.tileEntity = tile;
    	
        IItemHandler itemHandler = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        addSlotToContainer(new TapeDeckDiscSlot(itemHandler, 0, 31, 32, tile));
        addSlotToContainer(new TapeDeckCassetteSlot(itemHandler, 1, 125, 31, tile));
        layoutPlayerInventorySlots(8, 84, playerInventory);
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
	    return tileEntity.canInteractWith(playerIn);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		tileEntity.StopWrite();
	    ItemStack itemstack = ItemStack.EMPTY;
	    Slot slot = this.inventorySlots.get(index);
	    if (slot != null && slot.getHasStack()) {
	        ItemStack stack = slot.getStack();
	        itemstack = stack.copy();
	        if (index == 0 || index == 1) {
	            if (!this.mergeItemStack(stack, 2, 37, true)) {
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

	public boolean isWriting() {
        return this.tileEntity.isWriting();
	}

	public int getWriteTime(int p) {
		if(!this.inventorySlots.get(1).getHasStack()) return 0;
		Item it = this.inventorySlots.get(1).getStack().getItem();
        int i1 = tileEntity.getCurWriteTime();
        int i2 = 0;
		if(it instanceof AbstractAudioCassetteItem) {
			AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) it;
			i2 = c.getMaxWriteTime();
		}
        return i2 > 0 && i1 > 0 ? i1 * p / i2 : 0;
	}

	public BlockPos getPos() {
		return tileEntity.getPos();
	}
	
	public TileTapeDeck getTile() {
		return this.tileEntity;
	}

}