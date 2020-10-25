package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.tile.TileBoomBox;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class BoomBoxPrevSongPacket implements IMessage {
	
    private BlockPos pos;
    
    public BoomBoxPrevSongPacket() { }
    
    public BoomBoxPrevSongPacket(BlockPos pos) {
        this.pos = pos;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
    }

    public static class Handler implements IMessageHandler<BoomBoxPrevSongPacket, IMessage> {
        @Override
        public IMessage onMessage(BoomBoxPrevSongPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(BoomBoxPrevSongPacket message, MessageContext ctx) {
        	EntityPlayerMP playerMP = ctx.getServerHandler().player;
        	WorldServer sw = playerMP.getServerWorld();
        	TileBoomBox tile = (TileBoomBox)sw.getTileEntity(message.pos);
            if(tile.isPlaying) {tile.stopMusic(); tile.isPlaying = false;}
            tile.switchSong(false);
        }
    }
}
