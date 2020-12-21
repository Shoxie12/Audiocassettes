package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.WalkmanItem;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class WalkmanOnDropPacket{
	
	private final String id;
	
    public WalkmanOnDropPacket(PacketBuffer buf) {
    	id = buf.readString(10);
    }
	
	public WalkmanOnDropPacket(String id) {
		this.id = id.length() > 10 ? "-" : id;
    }
	
    public void toBytes(PacketBuffer buf) {
    	buf.writeString(id, 10);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	WalkmanItem.stopMusic(id,ctx.get().getSender(),true);
        });
        ctx.get().setPacketHandled(true);
    }
}