package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;


import com.shoxie.audiocassettes.entity.BoomBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

public class BoomBoxPrevSongPacket{
	
    private final BlockPos pos;
	
    public BoomBoxPrevSongPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
    }
	
	public BoomBoxPrevSongPacket(BlockPos pos) {
        this.pos = pos;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ServerLevel sw = ctx.get().getSender().serverLevel();
        	BoomBoxEntity entity = (BoomBoxEntity)sw.getBlockEntity(pos);
            if(entity.isPlaying) {entity.stopMusic(); entity.isPlaying = false;}
            entity.switchSong(false);
        });
        ctx.get().setPacketHandled(true);
    }
}