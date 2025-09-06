package com.shoxie.audiocassettes.entity;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.init.Init;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.menu.BoomBoxMenu;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.SBoomBoxPlayPacket;
import com.shoxie.audiocassettes.networking.SBoomBoxStopPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;


import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.NotNull;

public class BoomBoxEntity extends BlockEntity implements Nameable, MenuProvider {
    public int slotscnt = 1;
    private final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);
	public boolean isPlaying=false;
	public String id = "-";
	public String owneruid = "-";

    public BoomBoxEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(Init.BOOM_BOX_ENTITY.get(), p_155229_, p_155230_);

    }

    private final ItemStackHandler inventory = new ItemStackHandler(slotscnt) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            BoomBoxEntity.this.sendUpdates();
        }
    };

    public LazyOptional<ItemStackHandler> getInventoryOptional() {
        return this.inventoryOptional;
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inv, Player player) {
        return new BoomBoxMenu(i, inv, this);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if(this.getInventory() != null)
            this.inventory.deserializeNBT(tag.getCompound("cassette"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if(this.getInventory() != null)
            tag.put("cassette", this.getInventory().serializeNBT());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.inventoryOptional.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public Component getName() {
        return null;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.audiocassettes.boombox");
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
    
	public ItemStack getCassette() {
		return inventory.getStackInSlot(0);
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
    
    public void playMusic(ServerPlayer sender) {
    	
    	List<ServerPlayer> players = this.getLevel().getServer().getPlayerList().getPlayers();
    	owneruid = sender.getUUID().toString();
    	
    	this.isPlaying = true;
    	for(ServerPlayer player : players) {
    		if(
    				Math.abs(player.getX() - this.getBlockPos().getX()) < audiocassettes.BoomBoxMaxSoundDistance &&
    				Math.abs(player.getY() - this.getBlockPos().getY()) < audiocassettes.BoomBoxMaxSoundDistance &&
    				Math.abs(player.getZ() - this.getBlockPos().getZ()) < audiocassettes.BoomBoxMaxSoundDistance
    				)
    		{
    			boolean isowner = (player == sender);
				Networking.INSTANCE.sendTo(new SBoomBoxPlayPacket(this.getBlockPos(),this.getID(), isowner, this.getCassette()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    		}
    	}
    }
    
	public void stopMusic() {
    	List<ServerPlayer> players = this.level.getServer().getPlayerList().getPlayers();
    	for(ServerPlayer player : players) {
			Networking.INSTANCE.sendTo(new SBoomBoxStopPacket(this.getBlockPos(), this.getID()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    	}
	}

    public @NotNull ItemStackHandler getInventory() {
        return inventory;
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
