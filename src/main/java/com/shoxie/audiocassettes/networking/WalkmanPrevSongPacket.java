package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.WalkmanItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class WalkmanPrevSongPacket{
	
    public WalkmanPrevSongPacket(FriendlyByteBuf buf) {
    }
	
	public WalkmanPrevSongPacket() {
    }
	
    public void toBytes(FriendlyByteBuf buf) {
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ItemStack mp = WalkmanItem.getMPInHand(ctx.get().getSender());
        	WalkmanItem.switchSong(false, WalkmanItem.getCassette(mp));
            if(WalkmanItem.isPlaying(mp)) {
            	WalkmanItem.stopMusic(WalkmanItem.getID(mp),ctx.get().getSender(),false);
            	WalkmanItem.setPlaying(mp, false);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}