package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.tile.BoomBoxTile;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class CBoomBoxPlayPacket{
	
    private final BlockPos pos;
	
    public CBoomBoxPlayPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
    }
	
	public CBoomBoxPlayPacket(BlockPos pos) {
        this.pos = pos;
    }
	
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ServerWorld sw = ctx.get().getSender().getServerWorld();
        	BoomBoxTile tile = (BoomBoxTile)sw.getTileEntity(pos);
            tile.playMusic(ctx.get().getSender());
        });
        ctx.get().setPacketHandled(true);
    }
}