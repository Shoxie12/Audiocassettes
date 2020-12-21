package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.tile.TapeDeckTile;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class TapeDeckSetSongPacket{
	
    private final BlockPos pos;
	private final int song;
	
    public TapeDeckSetSongPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
    	song = buf.readInt();
    }
	
	public TapeDeckSetSongPacket(BlockPos pos,int song) {
        this.pos = pos;
		this.song = song;
    }
	
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(song);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ServerWorld sw = ctx.get().getSender().getServerWorld();
            TapeDeckTile tile = (TapeDeckTile)sw.getTileEntity(pos);
            tile.setSong(song);
        });
        ctx.get().setPacketHandled(true);
    }
}