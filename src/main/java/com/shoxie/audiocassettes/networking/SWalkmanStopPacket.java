package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.audiocassettes;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SWalkmanStopPacket{
	
	private final String id;
	private final boolean isowner;

    public SWalkmanStopPacket(PacketBuffer buf) {
        id = buf.readString();
        isowner = buf.readBoolean();
    }
	
	public SWalkmanStopPacket(String id, boolean isowner) {
		this.id = id;
		this.isowner = isowner;
    }
	
    public void toBytes(PacketBuffer buf) {
    	buf.writeString(id);
    	buf.writeBoolean(isowner);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            audiocassettes.proxy.WalkmanStop(id,isowner);
        });
        ctx.get().setPacketHandled(true);
    }
}