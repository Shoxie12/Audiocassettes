package com.shoxie.audiocassettes.container;

import com.shoxie.audiocassettes.ModContainers;
import com.shoxie.audiocassettes.slots.BoomBoxSlot;
import com.shoxie.audiocassettes.tile.BoomBoxTile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class BoomBoxContainer extends Container {

    private BoomBoxTile tile;
    private IItemHandler playerInventory;
	public int curr = 0;
	public int max = 0;
	public String title = "--Empty--";
	
    public BoomBoxContainer(int windowId, BoomBoxTile tile, PlayerInventory playerInventory) {
        super(ModContainers.CONTAINER_BOOM_BOX, windowId);
        this.tile = tile;
        this.playerInventory = new InvWrapper(playerInventory);
        this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
        	addSlot(new BoomBoxSlot(h, 0, 51, 17, this));
        });
        layoutPlayerInventorySlots(8, 65);
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

	public BlockPos getPos() {
		return this.tile.getPos();
	}
	
	public BoomBoxTile getTile() {
		return this.tile;
	}
	
	public boolean isPlaying()
	{
		return this.tile.isPlaying;
	}
}