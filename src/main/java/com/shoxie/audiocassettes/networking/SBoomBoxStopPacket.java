package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.audiocassettes;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SBoomBoxStopPacket{
	
    private final BlockPos pos;
    private final String id;
	
    public SBoomBoxStopPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
        id = buf.readString();
    }
	
	public SBoomBoxStopPacket(BlockPos pos, String id) {
        this.pos = pos;
        this.id = id;
    }
	
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeString(id);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            audiocassettes.proxy.BoomBoxStop(pos, id);
        });
        ctx.get().setPacketHandled(true);
    }
}