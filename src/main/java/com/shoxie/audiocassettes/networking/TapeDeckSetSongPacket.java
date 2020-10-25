package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.tile.TileTapeDeck;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TapeDeckSetSongPacket implements IMessage {
	
    private BlockPos pos;
	private int song;
	
	public TapeDeckSetSongPacket() { }
	
	public TapeDeckSetSongPacket(BlockPos pos,int song) {
        this.pos = pos;
		this.song = song;
    }
	
    @Override
    public void fromBytes(ByteBuf buf) {
    	pos = BlockPos.fromLong(buf.readLong());
    	song = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(song);
    }
	
    public static class Handler implements IMessageHandler<TapeDeckSetSongPacket, IMessage> {
        @Override
        public IMessage onMessage(TapeDeckSetSongPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(TapeDeckSetSongPacket message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.getEntityWorld();
            if (world.isBlockLoaded(message.pos)) {
                TileTapeDeck tile = (TileTapeDeck)world.getTileEntity(message.pos);
                tile.setSong(message.song);
            }
        }
    }
}