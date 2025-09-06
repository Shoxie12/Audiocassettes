package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.WalkmanItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;


public class CWalkmanStopPacket{
	
    public CWalkmanStopPacket(FriendlyByteBuf buf) {
    }
	
	public CWalkmanStopPacket() {
    }
	
    public void toBytes(FriendlyByteBuf buf) {
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ItemStack mp = WalkmanItem.getMPInHand(ctx.get().getSender());
        	WalkmanItem.stopMusic(WalkmanItem.getID(mp), ctx.get().getSender(),false);
        });
        ctx.get().setPacketHandled(true);
    }
}