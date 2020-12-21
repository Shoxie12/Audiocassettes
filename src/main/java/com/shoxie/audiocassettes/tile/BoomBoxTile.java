package com.shoxie.audiocassettes.tile;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.shoxie.audiocassettes.ModTileEntities;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.container.BoomBoxContainer;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.SBoomBoxPlayPacket;
import com.shoxie.audiocassettes.networking.SBoomBoxStopPacket;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BoomBoxTile extends TileEntity implements INamedContainerProvider {
	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
	public boolean isPlaying=false;
	public String id = "-";
	public String owneruid = "-";
	
    public BoomBoxTile() {
		super(ModTileEntities.TILE_BOOMBOX);
	}

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new BoomBoxContainer(i, this, playerInventory);
    }

    @Override
    public void read(CompoundNBT tag) {
	    CompoundNBT compound = tag.getCompound("cassette");
	    handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(compound));
	    super.read(tag);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT tag) {
	    handler.ifPresent(h -> {
		    CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
		    tag.put("cassette", compound);
	    });
    return super.write(tag);
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
			return new TranslationTextComponent("block.audiocassettes.boombox");
		return null;
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
		handleUpdateTag(pkt.getNbtCompound());
	}
	
    private IItemHandler createHandler() {
        return new ItemStackHandler(1) {

            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }
    
	public ItemStack getCassette() {
		final ItemStack[] it = new ItemStack[1];
		    handler.ifPresent(h -> {
		    it[0] = h.getStackInSlot(0);
		});
		return it[0];
	}
    
	public void setSong(int song) {
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
	
    public void playMusic(ServerPlayerEntity sender) {
    	
    	List<ServerPlayerEntity> players = this.getWorld().getServer().getPlayerList().getPlayers();
    	owneruid = sender.getUniqueID().toString();
    	
    	this.isPlaying = true;
    	for(ServerPlayerEntity player : players) {
    		if(
    				Math.abs(player.getPosX() - this.getPos().getX()) < audiocassettes.BoomBoxMaxSoundDistance &&
    				Math.abs(player.getPosY() - this.getPos().getY()) < audiocassettes.BoomBoxMaxSoundDistance &&
    				Math.abs(player.getPosZ() - this.getPos().getZ()) < audiocassettes.BoomBoxMaxSoundDistance
    				)
    		{
    			boolean isowner = (player == sender);
				Networking.INSTANCE.sendTo(new SBoomBoxPlayPacket(this.getPos(),this.getID(), isowner, this.getCassette()), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    		}
    	}
    }
    
	public void stopMusic() {
    	List<ServerPlayerEntity> players = this.getWorld().getServer().getPlayerList().getPlayers();
    	for(ServerPlayerEntity player : players) {
			Networking.INSTANCE.sendTo(new SBoomBoxStopPacket(this.getPos(), this.getID()), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
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
