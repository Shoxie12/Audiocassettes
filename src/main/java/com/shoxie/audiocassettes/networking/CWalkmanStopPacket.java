package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.WalkmanItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CWalkmanStopPacket{
	
    public CWalkmanStopPacket(PacketBuffer buf) {
    }
	
	public CWalkmanStopPacket() {
    }
	
    public void toBytes(PacketBuffer buf) {
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ItemStack mp = WalkmanItem.getMPInHand(ctx.get().getSender());
        	WalkmanItem.stopMusic(mp, ctx.get().getSender().getServerWorld(),ctx.get().getSender());
        });
        ctx.get().setPacketHandled(true);
    }
}