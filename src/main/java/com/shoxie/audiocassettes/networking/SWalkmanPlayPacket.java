package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SWalkmanPlayPacket{
	
    private final String id;
    private final String playerid;
    private final ItemStack cassette;
    private final boolean isowner;
	
    public SWalkmanPlayPacket(PacketBuffer buf) {
        id = buf.readString();
        playerid = buf.readString();
        isowner = buf.readBoolean();
        cassette = buf.readItemStack();
    }
	
	public SWalkmanPlayPacket(String id, String playerid, boolean isowner, ItemStack cassette) {
        this.id = id;
        this.playerid = playerid;
        this.isowner = isowner;
        this.cassette = cassette;
    }
	
    public void toBytes(PacketBuffer buf) {
        buf.writeString(id);
        buf.writeString(playerid);
        buf.writeBoolean(isowner);
        buf.writeItemStack(cassette);
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