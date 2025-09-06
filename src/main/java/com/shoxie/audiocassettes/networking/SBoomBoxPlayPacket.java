package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;


public class SBoomBoxPlayPacket{
	
	private final BlockPos pos;
	private final String id;
	private final boolean isowner;
	private final ItemStack cassette;
	
    public SBoomBoxPlayPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        id = buf.readUtf();
        isowner = buf.readBoolean();
        cassette = buf.readItem();
    }
	
	public SBoomBoxPlayPacket(BlockPos pos, String id, boolean isowner, ItemStack cassette) {
        this.pos = pos;
        this.id = id;
        this.isowner = isowner;
        this.cassette = cassette;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(id);
        buf.writeBoolean(isowner);
        buf.writeItemStack(cassette,false);
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