package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.WalkmanItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class WalkmanOnDropPacket{
	
	private final String id;
	
    public WalkmanOnDropPacket(FriendlyByteBuf buf) {
    	id = buf.readUtf(10);
    }
	
	public WalkmanOnDropPacket(String id) {
		this.id = id.length() > 10 ? "-" : id;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
    	buf.writeUtf(id, 10);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	WalkmanItem.stopMusic(id,ctx.get().getSender(),true);
        });
        ctx.get().setPacketHandled(true);
    }
}