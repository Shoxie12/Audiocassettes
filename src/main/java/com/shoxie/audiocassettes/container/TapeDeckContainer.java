package com.shoxie.audiocassettes.container;

import com.shoxie.audiocassettes.ModContainers;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.slots.TapeDeckCassetteSlot;
import com.shoxie.audiocassettes.slots.TapeDeckDiscSlot;
import com.shoxie.audiocassettes.tile.TapeDeckTile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class TapeDeckContainer extends Container {

    private TapeDeckTile tileEntity;
    private IItemHandler playerInventory;
    public TapeDeckContainer(int windowId, TapeDeckTile tile, PlayerInventory playerInventory) {
        super(ModContainers.CONTAINER_TAPE_DECK, windowId);
        tileEntity = tile;
        this.playerInventory = new InvWrapper(playerInventory);
        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
        	addSlot(new TapeDeckDiscSlot(h, 0, 31, 32, tile));
        	addSlot(new TapeDeckCassetteSlot(h, 1, 125, 31, tile));
        });
        layoutPlayerInventorySlots(8, 84);
        }
        
        
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
	    return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		tileEntity.stopWrite();
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


    @OnlyIn(Dist.CLIENT)
	public boolean isWriting() {
        return this.tileEntity.isWriting();
	}

	public int getWriteTime(int p) {
		Item it = this.inventorySlots.get(1).getStack().getItem();
        int i1 = tileEntity.getCurWriteTime();
        int i2 = 0;
		if(it instanceof AbstractAudioCassetteItem) {
			AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) it;
			i2 = c.getMaxWriteTime();
		}
        return i2 > 0 && i1 > 0 ? i1 * p / i2 : 0;
	}

	public TapeDeckTile getTile() {
		return this.tileEntity;
	}
	
	public BlockPos getPos() {
		return tileEntity.getPos();
	}

}