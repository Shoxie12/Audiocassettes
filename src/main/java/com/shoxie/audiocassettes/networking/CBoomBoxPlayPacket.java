package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;


import com.shoxie.audiocassettes.entity.BoomBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

public class CBoomBoxPlayPacket{
	
    private final BlockPos pos;
	
    public CBoomBoxPlayPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
    }
	
	public CBoomBoxPlayPacket(BlockPos pos) {
        this.pos = pos;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ServerLevel sw = ctx.get().getSender().serverLevel();
        	BoomBoxEntity entity = (BoomBoxEntity)sw.getBlockEntity(pos);
            entity.playMusic(ctx.get().getSender());
        });
        ctx.get().setPacketHandled(true);
    }
}