package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.item.WalkmanItem;
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
        	if(id.equals("-")) {
        		mp = WalkmanItem.getMPInHand(ctx.get().getSender()); 
                WalkmanItem.setPlaying(mp, false); 
                WalkmanItem.stopMusic(mp, ctx.get().getSender().getServerWorld(),ctx.get().getSender());
        		WalkmanItem.setLoop(mp, false);
        	}
        	else mp = WalkmanItem.getMPbyID(ctx.get().getSender(), id);
        	if(mp == ItemStack.EMPTY) return;
        	ItemStack cassette = WalkmanItem.getCassette(mp);
            int song = AbstractAudioCassetteItem.getCurrentSlot(cassette);
            int max = AbstractAudioCassetteItem.getMaxSlots(cassette);
            
            if(song < max)
            	AbstractAudioCassetteItem.setActiveSlot(song+1, cassette);
            else if(song > max)
            	AbstractAudioCassetteItem.setActiveSlot(max, cassette);
            
            if(!(id.equals("-")) && song < max) WalkmanItem.playMusic(mp, ctx.get().getSender().getServerWorld(), ctx.get().getSender());
        });
        ctx.get().setPacketHandled(true);
    }
}