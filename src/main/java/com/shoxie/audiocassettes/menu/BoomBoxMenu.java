package com.shoxie.audiocassettes.menu;

import com.shoxie.audiocassettes.init.Init;
import com.shoxie.audiocassettes.slots.BoomBoxSlot;
import com.shoxie.audiocassettes.entity.BoomBoxEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BoomBoxMenu extends AbstractContainerMenu {

    private BoomBoxEntity entity;
	
    public BoomBoxMenu(int windowId, Inventory inv, BlockEntity _entity) {
        super(Init.BOOMBOX_MENU.get(), windowId);
        if (_entity instanceof BoomBoxEntity e) {
            this.entity = e;
        } else {
            throw new IllegalStateException("Block entity (%s) is not BoomBoxEntity!"
                    .formatted(_entity.getClass().getCanonicalName()));
        }
        entity.getInventoryOptional().ifPresent(
                inventory -> addSlot(new BoomBoxSlot(inventory, 0, 79, 41, this)));
        layoutPlayerInventorySlots(inv,8, 65);
        }

    public BoomBoxMenu(int windowId, Inventory inv, FriendlyByteBuf buf) {
        this(windowId, inv, inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

@Override
public ItemStack quickMoveStack(Player playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);
    if (!slot.getItem().isEmpty()) {
        ItemStack stack = slot.getItem();
        itemstack = stack.copy();
        if (index == 0) {
            if (!this.moveItemStackTo(stack, 1, 37, true)) {
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

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
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

	public BlockPos getPos() {
		return this.entity.getBlockPos();
	}
	
	public BoomBoxEntity getEntity() {
		return this.entity;
	}
	
	public boolean isPlaying()
	{
		return this.entity.isPlaying;
	}
}