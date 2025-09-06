package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.audiocassettes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SWalkmanStopPacket{
	
	private final String id;
	private final boolean isowner;

    public SWalkmanStopPacket(FriendlyByteBuf buf) {
        id = buf.readUtf();
        isowner = buf.readBoolean();
    }
	
	public SWalkmanStopPacket(String id, boolean isowner) {
		this.id = id;
		this.isowner = isowner;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
    	buf.writeUtf(id);
    	buf.writeBoolean(isowner);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            audiocassettes.proxy.WalkmanStop(id,isowner);
        });
        ctx.get().setPacketHandled(true);
    }
}