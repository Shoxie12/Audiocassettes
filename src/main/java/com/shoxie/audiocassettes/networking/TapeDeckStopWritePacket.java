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

public class TapeDeckStopWritePacket implements IMessage {
	
    private BlockPos pos;

    public TapeDeckStopWritePacket() { }
    
    public TapeDeckStopWritePacket(BlockPos pos) {
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

    public static class Handler implements IMessageHandler<TapeDeckStopWritePacket, IMessage> {
        @Override
        public IMessage onMessage(TapeDeckStopWritePacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(TapeDeckStopWritePacket message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.getEntityWorld();
            if (world.isBlockLoaded(message.pos)) {
                TileTapeDeck tile = (TileTapeDeck)world.getTileEntity(message.pos);
                tile.StopWrite();
            }
        }
    }
}
