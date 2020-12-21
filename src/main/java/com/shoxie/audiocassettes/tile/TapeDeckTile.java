package com.shoxie.audiocassettes.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.shoxie.audiocassettes.ModTileEntities;
import com.shoxie.audiocassettes.container.TapeDeckContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TapeDeckTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

	public static final int Slotscnt = 2;
	private boolean Started = false;
	private int WriteTime=0;
	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
	ResourceLocation res;
	String sname;
	private boolean erase = false;
	
    public TapeDeckTile() {
		super(ModTileEntities.TILE_TAPE_DECK);
	}
    
    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new TapeDeckContainer(i, this, playerInventory);
    }
	
    public void StartWrite(ResourceLocation res, String sname, boolean erase) {
    	if(
    			(
    					this.getItemInSlot(0).getItem() instanceof MusicDiscItem ||
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
    
    public int getCurWriteTime() {
		return WriteTime;
    }
    
    public int getSizeInventory() {
        return Slotscnt;
     }
    
    @Override
    public void read(BlockState p_230337_1_, CompoundNBT tag) {
	    CompoundNBT compound = tag.getCompound("inv");
	    WriteTime = tag.getInt("WriteTime");
	    handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(compound));
	    super.read(p_230337_1_, tag);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT tag) {
	    handler.ifPresent(h -> {
		    CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
		    tag.put("inv", compound);
		    tag.putInt("WriteTime", WriteTime);
	    });
    return super.write(tag);
    }

    public void tick() {
    	if(isWriting()) {
    		--WriteTime;
    		sendUpdates();
    	}
    	else {
	    	if (!this.world.isRemote) {
		    	ItemStack disc = this.getItemInSlot(0);
		    	ItemStack cassette = this.getItemInSlot(1);
		    	if(
				    !cassette.isEmpty() &&
				    ((!disc.isEmpty() && disc.getItem() instanceof MusicDiscItem) || erase) && 
			    	cassette.getItem() instanceof AbstractAudioCassetteItem &&
			    	Started
		    	)
		    	FinaliseWrite(cassette, erase);
	    	}
    	}
    }
    
    private void FinaliseWrite(ItemStack cassette, boolean erase) {
        if(!erase)
    	    if(!(cassette.getItem() instanceof AbstractAudioCassetteItem))
    	    	return;
        	
    	AbstractAudioCassetteItem.appendSongs(cassette, res,sname);
		Started=false;
	}

	@Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
        	return handler.cast();
        }
        return super.getCapability(cap, side);
    }
    
	@Override
	public ITextComponent getDisplayName() {
		if (!world.isRemote) 
			return new TranslationTextComponent("block.audiocassettes.tapedeck");
		return null;
	}
	
	private ItemStack getItemInSlot(int itnum) {
		final ItemStack[] it = new ItemStack[1];
		    handler.ifPresent(h -> {
		    it[0] = h.getStackInSlot(itnum);
		});
		return it[0];
	}
	
	public void sendUpdates() {
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
		markDirty();
	}
	
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(getBlockState(), pkt.getNbtCompound());
	}
	
    private IItemHandler createHandler() {
        return new ItemStackHandler(Slotscnt) {

            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
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
}
