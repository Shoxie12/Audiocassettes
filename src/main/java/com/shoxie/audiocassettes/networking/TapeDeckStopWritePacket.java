package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.tile.TapeDeckTile;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class TapeDeckStopWritePacket{
	
    private final BlockPos pos;

    public TapeDeckStopWritePacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
        
    }

    public TapeDeckStopWritePacket(BlockPos pos) {
        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
    	
        ctx.get().enqueueWork(() -> {
        	ServerWorld sw = ctx.get().getSender().getServerWorld();
            TapeDeckTile tile = (TapeDeckTile)sw.getTileEntity(pos);
            tile.stopWrite();
        });
        ctx.get().setPacketHandled(true);
    }
}
