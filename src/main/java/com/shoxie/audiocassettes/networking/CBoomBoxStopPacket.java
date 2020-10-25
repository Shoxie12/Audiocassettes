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

public class CBoomBoxStopPacket implements IMessage {
	
    private BlockPos pos;
    
    public CBoomBoxStopPacket() { }
    
    public CBoomBoxStopPacket(BlockPos pos) {
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

    public static class Handler implements IMessageHandler<CBoomBoxStopPacket, IMessage> {
        @Override
        public IMessage onMessage(CBoomBoxStopPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(CBoomBoxStopPacket message, MessageContext ctx) {
            EntityPlayerMP playerMP = ctx.getServerHandler().player;
            WorldServer world = playerMP.getServerWorld();
            if (world.isBlockLoaded(message.pos)) {
            	TileBoomBox tile = (TileBoomBox) world.getTileEntity(message.pos);
                tile.stopMusic();
            }
        }
    }
}
