package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import com.shoxie.audiocassettes.tile.BoomBoxTile;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class BoomBoxNextSongPacket{
	
    private final BlockPos pos;
    private final boolean manually;
	
    public BoomBoxNextSongPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
        manually = buf.readBoolean();
    }
	
    public BoomBoxNextSongPacket(BlockPos pos, boolean manually) {
        this.pos = pos;
        this.manually = manually;
    }
	
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(manually);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerWorld sw = ctx.get().getSender().getServerWorld();
            BoomBoxTile tile = (BoomBoxTile)sw.getTileEntity(pos);
	        if(ctx.get().getSender().getUniqueID().toString().equals(tile.owneruid) || manually) {
	            ItemStack cassette = tile.getCassette();
	            int song = AbstractAudioCassetteItem.getCurrentSlot(cassette);
	            int max = AbstractAudioCassetteItem.getMaxSlots(cassette);
	            if(song < max)
	            	tile.setSong(song+1);
	            else if(song > max)
	            	tile.setSong(max);
	            if(manually) {
	            	if(tile.isPlaying) {
	            		tile.stopMusic(sw);
	            		tile.isPlaying = false;
	            	}
	            }
	            else if(song < max) tile.playMusic(ctx.get().getSender());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}