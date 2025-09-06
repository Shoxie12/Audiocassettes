package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class SWalkmanPlayPacket{
	
    private final String id;
    private final String playerid;
    private final ItemStack cassette;
    private final boolean isowner;
	
    public SWalkmanPlayPacket(FriendlyByteBuf buf) {
        id = buf.readUtf();
        playerid = buf.readUtf();
        isowner = buf.readBoolean();
        cassette = buf.readItem();
    }
	
	public SWalkmanPlayPacket(String id, String playerid, boolean isowner, ItemStack cassette) {
        this.id = id;
        this.playerid = playerid;
        this.isowner = isowner;
        this.cassette = cassette;
    }
	
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(id);
        buf.writeUtf(playerid);
        buf.writeBoolean(isowner);
        buf.writeItemStack(cassette,false);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	if(cassette.getItem() instanceof AbstractAudioCassetteItem)
        		audiocassettes.proxy.WalkmanPlay(id,playerid,isowner,
        				AbstractAudioCassetteItem.getCurrentSong(cassette),
        				AbstractAudioCassetteItem.getSongTitle(cassette)
        	);
        });
        ctx.get().setPacketHandled(true);
    }
}