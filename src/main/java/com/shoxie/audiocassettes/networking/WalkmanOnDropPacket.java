package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.item.WalkmanItem;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class WalkmanOnDropPacket implements IMessage {
	
	private String mpid;
    
	
	public WalkmanOnDropPacket() { }
	
    public WalkmanOnDropPacket(String mpid) {
        this.mpid = mpid.length() > 127 ? "0" : mpid;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	mpid = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	ByteBufUtils.writeUTF8String(buf, mpid);
    }

    public static class Handler implements IMessageHandler<WalkmanOnDropPacket, IMessage> {
        @Override
        public IMessage onMessage(WalkmanOnDropPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(WalkmanOnDropPacket message, MessageContext ctx) {
        	WalkmanItem.stopMusic(message.mpid,ctx.getServerHandler().player,true);
        }
    }
}
