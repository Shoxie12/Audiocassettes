package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.item.WalkmanItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class WalkmanPrevSongPacket{
	
    public WalkmanPrevSongPacket(PacketBuffer buf) {
    }
	
	public WalkmanPrevSongPacket() {
    }
	
    public void toBytes(PacketBuffer buf) {
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ItemStack mp = WalkmanItem.getMPInHand(ctx.get().getSender());
        	ItemStack cassette = WalkmanItem.getCassette(mp);
        	
        	WalkmanItem.setLoop(mp, false);
            int song = AbstractAudioCassetteItem.getCurrentSlot(cassette);
            WalkmanItem.setPlaying(mp, false); 
            WalkmanItem.stopMusic(mp, ctx.get().getSender().getServerWorld(),ctx.get().getSender());
            
            if(song > 1)
            	AbstractAudioCassetteItem.setActiveSlot(song-1, cassette);
            else if(song < 1)
            	AbstractAudioCassetteItem.setActiveSlot(1, cassette);
        	
        });
        ctx.get().setPacketHandled(true);
    }
}