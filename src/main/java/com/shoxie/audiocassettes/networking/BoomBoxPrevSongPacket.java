package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.tile.BoomBoxTile;

import net.minecraft.item.ItemStack;
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
            ItemStack cassette = tile.getCassette();
            int song = AbstractAudioCassetteItem.getCurrentSlot(cassette);
            if(tile.isPlaying) {tile.stopMusic(sw); tile.isPlaying = false;}
            if(song > 1)
            	tile.setSong(song-1);
            else if(song < 1)
            	tile.setSong(1);
        });
        ctx.get().setPacketHandled(true);
    }
}