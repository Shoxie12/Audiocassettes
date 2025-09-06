package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.audiocassettes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;


public class SBoomBoxStopPacket{
	
    private final BlockPos pos;
    private final String id;
	
    public SBoomBoxStopPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        id = buf.readUtf();
    }
	
	public SBoomBoxStopPacket(BlockPos pos, String id) {
        this.pos = pos;
        this.id = id;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(id);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            audiocassettes.proxy.BoomBoxStop(pos, id);
        });
        ctx.get().setPacketHandled(true);
    }
}