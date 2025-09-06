package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.WalkmanItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class CWalkmanPlayPacket{
	private final String id;
	
    public CWalkmanPlayPacket(FriendlyByteBuf buf) {
		id = buf.readUtf(16);
    }
	
	public CWalkmanPlayPacket(String id) {
		this.id = id;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
    	buf.writeUtf(id);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ItemStack mp = ItemStack.EMPTY;
        	if(id.equals("-")) mp = WalkmanItem.getMPInHand(ctx.get().getSender());
        	else mp = WalkmanItem.getMPbyID(ctx.get().getSender(), id);
        	if(mp != ItemStack.EMPTY)
        		WalkmanItem.playMusic(mp, ctx.get().getSender());
        });
        ctx.get().setPacketHandled(true);
    }
}