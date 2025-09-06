package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.entity.TapeDeckEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

public class TapeDeckStopWritePacket{
	
    private final BlockPos pos;

    public TapeDeckStopWritePacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        
    }

    public TapeDeckStopWritePacket(BlockPos pos) {
        this.pos = pos;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
    	
        ctx.get().enqueueWork(() -> {
        	ServerLevel sw = ctx.get().getSender().serverLevel();
            TapeDeckEntity entity = (TapeDeckEntity)sw.getBlockEntity(pos);
            entity.stopWrite();
        });
        ctx.get().setPacketHandled(true);
    }
}
