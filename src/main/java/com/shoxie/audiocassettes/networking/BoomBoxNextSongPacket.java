package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.entity.BoomBoxEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

public class BoomBoxNextSongPacket{
	
    private final BlockPos pos;
    private final boolean manually;
	
    public BoomBoxNextSongPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        manually = buf.readBoolean();
    }
	
    public BoomBoxNextSongPacket(BlockPos pos, boolean manually) {
        this.pos = pos;
        this.manually = manually;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(manually);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerLevel sw = ctx.get().getSender().serverLevel().getLevel();
            BoomBoxEntity entity = (BoomBoxEntity)sw.getBlockEntity(pos);
	        if(ctx.get().getSender().getUUID().toString().equals(entity.owneruid) || manually) {
	        	boolean switched = entity.switchSong(true);
	            if(manually) {
	            	if(entity.isPlaying) {
	            		entity.stopMusic();
	            		entity.isPlaying = false;
	            	}
	            }
	            else if(switched) entity.playMusic(ctx.get().getSender()); else entity.stopMusic();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}