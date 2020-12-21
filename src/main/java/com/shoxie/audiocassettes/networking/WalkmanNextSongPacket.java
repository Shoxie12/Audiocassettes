package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.WalkmanItem;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class WalkmanNextSongPacket{
	private final String id;
	
    public WalkmanNextSongPacket(PacketBuffer buf) {
		id = buf.readString(16);
    }
	
	public WalkmanNextSongPacket(String id) {
		this.id = id;
    }
	
    public void toBytes(PacketBuffer buf) {
    	buf.writeString(id);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ItemStack mp = ItemStack.EMPTY;
        	ServerPlayerEntity player = ctx.get().getSender();
        	
	    	if(id.equals("-")) {
	    		mp = WalkmanItem.getMPInHand(player); 
	    		if(WalkmanItem.isPlaying(mp)) {
		            WalkmanItem.stopMusic(WalkmanItem.getID(mp), player,false);
		    		WalkmanItem.setPlaying(mp, false);
	    		}
	    	}
	    	else mp = WalkmanItem.getMPbyID(player, id);
	    	if(mp == ItemStack.EMPTY) return;
            
	    	boolean switched = WalkmanItem.switchSong(true, WalkmanItem.getCassette(mp));
	        if(!(id.equals("-")) && switched) WalkmanItem.playMusic(mp, player.getServerWorld(), player);
        });
        ctx.get().setPacketHandled(true);
    }
}