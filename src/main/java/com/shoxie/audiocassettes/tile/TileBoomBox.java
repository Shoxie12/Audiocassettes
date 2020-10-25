package com.shoxie.audiocassettes.tile;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import com.shoxie.audiocassettes.ModConfig;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.SBoomBoxPlayPacket;
import com.shoxie.audiocassettes.networking.SBoomBoxStopPacket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileBoomBox extends TileEntity {
	public boolean isPlaying=false;
	public String id = "-";
	public String owneruid = "-";
	
    private ItemStackHandler itemStackHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
        	TileBoomBox.this.markDirty();
        }
    };

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("cassette")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("cassette"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
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
    
    
	public void sendUpdates() {
		world.notifyBlockUpdate(pos, world.getBlockState(this.getPos()), world.getBlockState(this.getPos()), 2);
		markDirty();
	}
	
	@Override
	public ITextComponent getDisplayName() {
		if (!world.isRemote) 
			return new TextComponentTranslation("block.audiocassettes.boombox");
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
    
	public ItemStack getCassette() {
		return itemStackHandler.getStackInSlot(0);
	}
    
	private void setSong(int song) {
		AbstractAudioCassetteItem.setActiveSlot(song, getCassette());
		sendUpdates();
	}
	
	public boolean switchSong(boolean forward) {
		
		if(audiocassettes.skipemptyslots) {
			int song = AbstractAudioCassetteItem.getNonEmptySlot(getCassette(),forward);
			if(song != -1) setSong(song);
			else return false;
		}
	    else {
				int cur=AbstractAudioCassetteItem.getCurrentSlot(getCassette());
				int max=AbstractAudioCassetteItem.getMaxSlots(getCassette());
				if(forward) {
			        if(cur < max)
			            this.setSong(cur+1);
			        else if(cur > max)
			            this.setSong(max);
			        else return false;
				}
				else {
		            if(cur > 1)
		            	this.setSong(cur-1);
		            else if(cur < 1)
		            	this.setSong(1);
		            else return false;
				}
	    }
		sendUpdates();
		return true;
	}
	
    public void playMusic(EntityPlayerMP sender) {
    	List<EntityPlayerMP> players = this.getWorld().getMinecraftServer().getPlayerList().getPlayers();
    	owneruid = sender.getUniqueID().toString();
    	
    	this.isPlaying = true;
    	for(EntityPlayerMP player : players) {
    		if(
    				Math.abs(player.getPosition().getX() - this.getPos().getX()) < ModConfig.boomboxbmaxdist &&
    				Math.abs(player.getPosition().getY() - this.getPos().getY()) < ModConfig.boomboxbmaxdist &&
    				Math.abs(player.getPosition().getZ() - this.getPos().getZ()) < ModConfig.boomboxbmaxdist
    				)
    		{
    			boolean isowner = (player == sender);
				Networking.INSTANCE.sendTo(new SBoomBoxPlayPacket(this.getPos(),this.getID(), isowner, this.getCassette()), player);
    		}
    	}
    }
    
	public void stopMusic() {
    	List<EntityPlayerMP> players = this.getWorld().getMinecraftServer().getPlayerList().getPlayers();
    	for(EntityPlayerMP player : players) {
			Networking.INSTANCE.sendTo(new SBoomBoxStopPacket(this.getPos(), this.getID()), player);
    	}
	}
	
	public String getID() {
	    if (this.id.equals("-")) { 
		    Random rand = new Random();
		    int randomNum = rand.nextInt(10000);
	        this.id = Integer.toString(randomNum);
	    }
	    return this.id;
	}
}
