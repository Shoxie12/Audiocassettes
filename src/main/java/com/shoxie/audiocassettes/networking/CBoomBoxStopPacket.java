package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.tile.BoomBoxTile;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class CBoomBoxStopPacket{
	
    private final BlockPos pos;
	
    public CBoomBoxStopPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
    }
	
	public CBoomBoxStopPacket(BlockPos pos) {
        this.pos = pos;
    }
	
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ServerWorld sw = ctx.get().getSender().getServerWorld();
        	BoomBoxTile tile = (BoomBoxTile)sw.getTileEntity(pos);
            tile.stopMusic(sw);
        });
        ctx.get().setPacketHandled(true);
    }
}