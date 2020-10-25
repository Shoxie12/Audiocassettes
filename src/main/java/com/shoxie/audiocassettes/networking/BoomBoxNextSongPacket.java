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

public class BoomBoxNextSongPacket implements IMessage {
	
    private BlockPos pos;
    private boolean manually;

    public BoomBoxNextSongPacket() { }
    
    public BoomBoxNextSongPacket(BlockPos pos,  boolean manually) {
        this.pos = pos;
        this.manually = manually;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	pos = BlockPos.fromLong(buf.readLong());
    	manually = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeBoolean(manually);
    }

    public static class Handler implements IMessageHandler<BoomBoxNextSongPacket, IMessage> {
        @Override
        public IMessage onMessage(BoomBoxNextSongPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(BoomBoxNextSongPacket message, MessageContext ctx) {
        	EntityPlayerMP playerMP = ctx.getServerHandler().player;
        	WorldServer sw = playerMP.getServerWorld();
            TileBoomBox tile = (TileBoomBox)sw.getTileEntity(message.pos);
	        if(playerMP.getUniqueID().toString().equals(tile.owneruid) || message.manually) {
	        	boolean switched = tile.switchSong(true);
	            if(message.manually) {
	            	if(tile.isPlaying) {
	            		tile.stopMusic();
	            		tile.isPlaying = false;
	            	}
	            }
	            else if(switched) tile.playMusic(playerMP); else tile.stopMusic();
            }
        }
    }
}
