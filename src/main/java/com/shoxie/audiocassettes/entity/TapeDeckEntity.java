package com.shoxie.audiocassettes.entity;

import javax.annotation.Nullable;

import com.shoxie.audiocassettes.init.Init;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;

import com.shoxie.audiocassettes.menu.TapeDeckMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class TapeDeckEntity extends BlockEntity implements Nameable, MenuProvider {

	public static final int Slotscnt = 2;
	private boolean Started = false;
	private int WriteTime=0;
    // 0 = Disc 1 = Cassette
    private final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);
    ResourceLocation res;
	String sname;
	private boolean erase = false;

    public TapeDeckEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(Init.TAPEDECK_ENTITY.get(), p_155229_, p_155230_);

    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inv, Player Player) {
        return new TapeDeckMenu(i, inv, this);
    }
	
    public void StartWrite(ResourceLocation res, String sname, boolean erase) {
    	if(
    			(
    					this.getItemInSlot(0).getItem() instanceof RecordItem ||
    					erase
    			)
    			&& this.getItemInSlot(1).getItem() instanceof AbstractAudioCassetteItem
	    )
    	{
    		AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) this.getItemInSlot(1).getItem();
	    	WriteTime = c.getMaxWriteTime();
	    	Started = true;
	    	this.res = res;
	    	this.sname = sname;
	    	this.erase = erase;
    	}
    }

    public LazyOptional<ItemStackHandler> getInventoryOptional() {
        return this.inventoryOptional;
    }

    private final ItemStackHandler inventory = new ItemStackHandler(Slotscnt) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            TapeDeckEntity.this.sendUpdates();
        }
    };

    public int getCurWriteTime() {
		return WriteTime;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("inv"));
        WriteTime = tag.getInt("WriteTime");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inv", this.inventory.serializeNBT());
        tag.putInt("WriteTime", WriteTime);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        TapeDeckEntity tde = (TapeDeckEntity) blockEntity;
        if(tde.isWriting()) {

    		--tde.WriteTime;
            tde.sendUpdates();
    	}
    	else {
		    	ItemStack disc = tde.getItemInSlot(0);
		    	ItemStack cassette = tde.getItemInSlot(1);
		    	if(
				    !cassette.isEmpty() &&
				    ((!disc.isEmpty() && disc.getItem() instanceof RecordItem) || tde.erase) &&
			    	cassette.getItem() instanceof AbstractAudioCassetteItem &&
                            tde.Started
		    	)
                    tde.FinaliseWrite(cassette, tde.erase);
    	}
    }
    
    private void FinaliseWrite(ItemStack cassette, boolean erase) {
        if(!erase)
    	    if(!(cassette.getItem() instanceof AbstractAudioCassetteItem))
    	    	return;
        	
    	AbstractAudioCassetteItem.appendSongs(cassette, res,sname);
		Started=false;
	}

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.inventoryOptional.cast();
        }
        return super.getCapability(cap);
    }
	
	private ItemStack getItemInSlot(int itnum) {
        return this.inventory.getStackInSlot(itnum);
	}

    @Override
    public Component getName() {
        return getDisplayName();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.audiocassettes.tapedeck");
    }

    public void sendUpdates() {
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

	public boolean isWriting() {
		return WriteTime > 0;
	}

	public void stopWrite() {
		Started = false;
		WriteTime = 0;
		sendUpdates();
		
	}

	public void setSong(int song) {
		AbstractAudioCassetteItem.setActiveSlot(song, this.getItemInSlot(1));
		sendUpdates();
	}

    public ItemStackHandler getInventory() {
        return this.inventory;
    }
}
