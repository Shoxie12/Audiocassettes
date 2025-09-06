package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.entity.TapeDeckEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

public class TapeDeckSetSongPacket{
	
    private final BlockPos pos;
	private final int song;
	
    public TapeDeckSetSongPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
    	song = buf.readInt();
    }
	
	public TapeDeckSetSongPacket(BlockPos pos,int song) {
        this.pos = pos;
		this.song = song;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(song);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ServerLevel sw = ctx.get().getSender().serverLevel().getLevel();
            TapeDeckEntity entity = (TapeDeckEntity)sw.getBlockEntity(pos);
            entity.setSong(song);
        });
        ctx.get().setPacketHandled(true);
    }
}