package com.shoxie.audiocassettes.menu;

import com.shoxie.audiocassettes.init.Init;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.slots.TapeDeckCassetteSlot;
import com.shoxie.audiocassettes.slots.TapeDeckDiscSlot;
import com.shoxie.audiocassettes.entity.TapeDeckEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TapeDeckMenu extends AbstractContainerMenu {

    private TapeDeckEntity entity;
    public TapeDeckMenu(int windowId, Inventory inv, BlockEntity _entity) {
        super(Init.TAPEDECK_MENU.get(), windowId);
        if (_entity instanceof TapeDeckEntity e) {
            this.entity = e;
        } else {
            throw new IllegalStateException("Block entity (%s) is not TapeDeckEntity!"
                    .formatted(_entity.getClass().getCanonicalName()));
        }
        entity.getInventoryOptional().ifPresent(inventory -> {
        	addSlot(new TapeDeckDiscSlot(inventory, 0, 31, 32, entity));
        	addSlot(new TapeDeckCassetteSlot(inventory, 1, 125, 31, entity));
        });
        layoutPlayerInventorySlots(inv,8, 84);
        }

    public TapeDeckMenu(int windowId, Inventory inv, FriendlyByteBuf buf) {
        this(windowId, inv, inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

	@Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
		entity.stopWrite();
	    ItemStack itemstack = ItemStack.EMPTY;
	    Slot slot = this.slots.get(index);
	    if (!slot.getItem().isEmpty()) {
	        ItemStack stack = slot.getItem();
	        itemstack = stack.copy();
	        if (index == 0 || index == 1) {
	            if (!this.moveItemStackTo(stack, 2, 37, true)) {
	                return ItemStack.EMPTY;
	            }
	            slot.onQuickCraft(stack, itemstack);
	        } else {
	            if (!this.moveItemStackTo(stack, 0, 2, false)) {
				    return ItemStack.EMPTY;
				}
	        }
	
	        if (stack.isEmpty()) {
	            slot.set(ItemStack.EMPTY);
	        } else {
	            slot.setChanged();
	        }
	
	        if (stack.getCount() == itemstack.getCount()) {
	            return ItemStack.EMPTY;
	        }
	
	        slot.onTake(playerIn, stack);
	    }
	
	    return itemstack;
	}


    private int addSlotRange(Inventory handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new Slot(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private void addSlotBox(Inventory inv, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(inv, index, x, y, horAmount, dx);
            y += dy;
        }
    }

    private void layoutPlayerInventorySlots(Inventory inv, int leftCol, int topRow) {
        addSlotBox(inv, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(inv, 0, leftCol, topRow, 9, 18);
    }


    @OnlyIn(Dist.CLIENT)
	public boolean isWriting() {
        return this.entity.isWriting();
	}

	public int getWriteTime(int p) {
		if(this.slots.get(1).getItem().isEmpty()) return 0;
		Item it = this.slots.get(1).getItem().getItem();
        int i1 = entity.getCurWriteTime();
        int i2 = 0;
		if(it instanceof AbstractAudioCassetteItem c) {
            i2 = c.getMaxWriteTime();
		}
        return i2 > 0 && i1 > 0 ? i1 * p / i2 : 0;
	}

	public TapeDeckEntity getEntity() {
		return this.entity;
	}
	
	public BlockPos getPos() {
		return entity.getBlockPos();
	}

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}