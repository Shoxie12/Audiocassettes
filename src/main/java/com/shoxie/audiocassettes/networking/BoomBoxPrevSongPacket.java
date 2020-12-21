package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.tile.BoomBoxTile;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class BoomBoxPrevSongPacket{
	
    private final BlockPos pos;
	
    public BoomBoxPrevSongPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
    }
	
	public BoomBoxPrevSongPacket(BlockPos pos) {
        this.pos = pos;
    }
	
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ServerWorld sw = ctx.get().getSender().getServerWorld();
        	BoomBoxTile tile = (BoomBoxTile)sw.getTileEntity(pos);
            if(tile.isPlaying) {tile.stopMusic(); tile.isPlaying = false;}
            tile.switchSong(false);
        });
        ctx.get().setPacketHandled(true);
    }
}