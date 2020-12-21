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
    private final boolean erase;

    public TapeDeckStartWritingPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
        res = buf.readResourceLocation();
        sname = buf.readString(128);
        erase = buf.readBoolean();
        
    }

    public TapeDeckStartWritingPacket(BlockPos pos, ResourceLocation res, String sname, boolean erase) {
        this.pos = pos;
        this.res = res;
        this.sname = sname.length() > 127 ? "Untitled" : sname;
        this.erase = erase;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(res);
        buf.writeString(sname);
        buf.writeBoolean(erase);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
    	
        ctx.get().enqueueWork(() -> {
        	ServerWorld sw = ctx.get().getSender().getServerWorld();
            TapeDeckTile tile = (TapeDeckTile)sw.getTileEntity(pos);
            tile.StartWrite(res,sname,erase);
        });
        ctx.get().setPacketHandled(true);
    }
}
