package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.tile.TapeDeckTile;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class TapeDeckStartWritingPacket{
	
    private final BlockPos pos;
    private final ResourceLocation res;
    private final String sname;

    public TapeDeckStartWritingPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
        res = buf.readResourceLocation();
        sname = buf.readString(128);
        
    }

    public TapeDeckStartWritingPacket(BlockPos pos, ResourceLocation res, String sname) {
        this.pos = pos;
        this.res = res;
        this.sname = sname.length() > 127 ? "Untitled" : sname;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(res);
        buf.writeString(sname);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
    	
        ctx.get().enqueueWork(() -> {
        	ServerWorld sw = ctx.get().getSender().getServerWorld();
            TapeDeckTile tile = (TapeDeckTile)sw.getTileEntity(pos);
            tile.StartWrite(res,sname);
        });
        ctx.get().setPacketHandled(true);
    }
}
