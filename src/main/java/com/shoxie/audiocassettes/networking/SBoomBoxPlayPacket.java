package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SBoomBoxPlayPacket{
	
	private final BlockPos pos;
	private final String id;
	private final boolean isowner;
	private final ItemStack cassette;
	
    public SBoomBoxPlayPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
        id = buf.readString();
        isowner = buf.readBoolean();
        cassette = buf.readItemStack();
    }
	
	public SBoomBoxPlayPacket(BlockPos pos, String id, boolean isowner, ItemStack cassette) {
        this.pos = pos;
        this.id = id;
        this.isowner = isowner;
        this.cassette = cassette;
    }
	
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeString(id);
        buf.writeBoolean(isowner);
        buf.writeItemStack(cassette);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	if(cassette.getItem() instanceof AbstractAudioCassetteItem)
        		audiocassettes.proxy.BoomBoxPlay(pos,id,isowner,
        				AbstractAudioCassetteItem.getCurrentSong(cassette),
        				AbstractAudioCassetteItem.getSongTitle(cassette)
        	);
        });
        ctx.get().setPacketHandled(true);
    }
}