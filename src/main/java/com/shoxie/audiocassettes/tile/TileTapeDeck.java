package com.shoxie.audiocassettes.tile;

import javax.annotation.Nullable;

import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileTapeDeck extends TileEntity implements ITickable{

	public static final int Slotscnt = 2;
	private boolean Started = false;
	private int WriteTime=0;
	String song;
	String sname;
	boolean erase=false;
	

    private ItemStackHandler itemStackHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
        	TileTapeDeck.this.markDirty();
        }
    };

    public ItemStackHandler getHandler() {
    	return this.itemStackHandler;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        WriteTime = compound.getInteger("WriteTime");
        if (compound.hasKey("cassette")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("cassette"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("WriteTime", WriteTime);
        compound.setTag("cassette", itemStackHandler.serializeNBT());
        return compound;
    }
    
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);
        }
        return super.getCapability(capability, facing);
    }
    
	public ItemStack getCassette() {
		return itemStackHandler.getStackInSlot(1);
	}

    public void StartWrite(String song, String sname, boolean erase) {
    	if(
    			(
    					itemStackHandler.getStackInSlot(0).getItem() instanceof ItemRecord ||
    					erase
    			)
    			&& itemStackHandler.getStackInSlot(1).getItem() instanceof AbstractAudioCassetteItem
	    )
    	{
    		AbstractAudioCassetteItem c = (AbstractAudioCassetteItem) itemStackHandler.getStackInSlot(1).getItem();
	    	WriteTime = c.getMaxWriteTime();
	    	Started = true;
	    	this.song = song;
	    	this.sname = sname;
	    	this.erase = erase;
    	}
    }
    
    private void FinaliseWrite(ItemStack cassette, boolean erase) {
    	if(!erase)
	    	if(!(cassette.getItem() instanceof AbstractAudioCassetteItem))
	    		return;
    	
    	AbstractAudioCassetteItem.appendSongs(cassette, song,sname);
		Started=false;
	}
    
	public void sendUpdates() {
		world.notifyBlockUpdate(pos, world.getBlockState(this.getPos()), world.getBlockState(this.getPos()), 2);
		markDirty();
	}
	
	@Override
	public ITextComponent getDisplayName() {
		if (!world.isRemote) 
			return new TextComponentTranslation("block.audiocassettes.tapedeck");
		return null;
	}
	
	@Override
	@Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}
	
    public int getCurWriteTime() {
		return WriteTime;
    }
    
    public int getSizeInventory() {
        return Slotscnt;
     }
	
	public boolean isWriting() {
		return WriteTime > 0;
	}

	public void StopWrite() {
		Started = false;
		WriteTime = 0;
		sendUpdates();
	}

	public void setSong(int song) {
		AbstractAudioCassetteItem.setActiveSlot(song, itemStackHandler.getStackInSlot(1));
	}

	@Override
	public void update() {
    	if(isWriting()) {
    		--WriteTime;
    		sendUpdates();
    	}
    	else {
	    	if (!this.world.isRemote) {
		    	ItemStack disc = itemStackHandler.getStackInSlot(0);
		    	ItemStack cassette = itemStackHandler.getStackInSlot(1);
		    	if(
			    	!cassette.isEmpty() &&
			    	((!disc.isEmpty() && disc.getItem() instanceof ItemRecord) || erase) && 
			    	cassette.getItem() instanceof AbstractAudioCassetteItem &&
			    	Started
		    	)
		    	FinaliseWrite(cassette,erase);
	    	}
    	}
	}
}
