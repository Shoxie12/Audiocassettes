package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.item.WalkmanItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class WalkmanOnDropPacket{
	
	private final ItemStack mp;
	
    public WalkmanOnDropPacket(PacketBuffer buf) {
    	mp = buf.readItemStack();
    }
	
	public WalkmanOnDropPacket(ItemStack mp) {
		this.mp = mp;
    }
	
    public void toBytes(PacketBuffer buf) {
    	buf.writeItemStack(mp);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	if(mp.getItem() instanceof WalkmanItem)
        		WalkmanItem.stopMusic(mp, ctx.get().getSender().getServerWorld(),null);
        });
        ctx.get().setPacketHandled(true);
    }
}