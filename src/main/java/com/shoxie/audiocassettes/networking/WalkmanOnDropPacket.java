package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.item.WalkmanItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class WalkmanOnDropPacket implements IMessage {
	
	private ItemStack mp;
    
	public WalkmanOnDropPacket() { }
	
    public WalkmanOnDropPacket(ItemStack mp) {
        this.mp = mp;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	mp = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	ByteBufUtils.writeItemStack(buf, mp);
    }

    public static class Handler implements IMessageHandler<WalkmanOnDropPacket, IMessage> {
        @Override
        public IMessage onMessage(WalkmanOnDropPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(WalkmanOnDropPacket message, MessageContext ctx) {
        	if(message.mp.getItem() instanceof WalkmanItem)
        		WalkmanItem.stopMusic(message.mp, ctx.getServerHandler().player.getServerWorld(),ctx.getServerHandler().player);
        }
    }
}
