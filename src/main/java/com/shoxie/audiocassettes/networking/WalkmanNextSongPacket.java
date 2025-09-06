package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.WalkmanItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class WalkmanNextSongPacket{
	private final String id;
	
    public WalkmanNextSongPacket(FriendlyByteBuf buf) {
		id = buf.readUtf(16);
    }
	
	public WalkmanNextSongPacket(String id) {
		this.id = id;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
    	buf.writeUtf(id);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ItemStack mp = ItemStack.EMPTY;
        	ServerPlayer playerMP = ctx.get().getSender();
        	if(id.equals("-")) {
        		mp = WalkmanItem.getMPInHand(playerMP); 
	    		if(WalkmanItem.isPlaying(mp)) {
		            WalkmanItem.stopMusic(WalkmanItem.getID(mp),playerMP, false);
		    		WalkmanItem.setPlaying(mp, false);
	    		}
        	}
        	else mp = WalkmanItem.getMPbyID(playerMP, id);
        	if(mp == ItemStack.EMPTY) return;
        	
	    	boolean switched = WalkmanItem.switchSong(true, WalkmanItem.getCassette(mp));
	        if(!(id.equals("-")) && switched) WalkmanItem.playMusic(mp, playerMP);
        });
        ctx.get().setPacketHandled(true);
    }
}