package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.item.WalkmanItem;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CWalkmanStopPacket implements IMessage {

    public CWalkmanStopPacket() { }
    
	@Override
    public void fromBytes(ByteBuf buf) { }

    @Override
    public void toBytes(ByteBuf buf) { }

    public static class Handler implements IMessageHandler<CWalkmanStopPacket, IMessage> {
        @Override
        public IMessage onMessage(CWalkmanStopPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(CWalkmanStopPacket message, MessageContext ctx) {
        	ItemStack mp = WalkmanItem.getMPInHand(ctx.getServerHandler().player);
        	WalkmanItem.stopMusic(mp, ctx.getServerHandler().player.getServerWorld(),ctx.getServerHandler().player);
        }
    }
}
