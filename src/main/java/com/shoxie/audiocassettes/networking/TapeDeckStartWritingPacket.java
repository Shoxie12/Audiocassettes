package com.shoxie.audiocassettes.networking;

import java.util.function.Supplier;

import com.shoxie.audiocassettes.entity.TapeDeckEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;


public class TapeDeckStartWritingPacket{
	
    private final BlockPos pos;
    private final ResourceLocation res;
    private final String sname;
    private final boolean erase;

    public TapeDeckStartWritingPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        res = buf.readResourceLocation();
        sname = buf.readUtf(128);
        erase = buf.readBoolean();
        
    }

    public TapeDeckStartWritingPacket(BlockPos pos, ResourceLocation res, String sname, boolean erase) {
        this.pos = pos;
        this.res = res;
        this.sname = sname.length() > 127 ? "Untitled" : sname;
        this.erase = erase;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(res);
        buf.writeUtf(sname);
        buf.writeBoolean(erase);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
    	
        ctx.get().enqueueWork(() -> {
        	ServerLevel sw = ctx.get().getSender().serverLevel();
            TapeDeckEntity entity = (TapeDeckEntity) sw.getBlockEntity(pos);
            entity.StartWrite(res,sname,erase);
        });
        ctx.get().setPacketHandled(true);
    }
}
