package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.audiocassettes;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SWalkmanStopPacket implements IMessage {
	
    private String id;
    private boolean isowner;
    
    public SWalkmanStopPacket() { }
    
    public SWalkmanStopPacket(String id, boolean isowner) {
		this.id = id;
		this.isowner = isowner;
	}

	@Override
    public void fromBytes(ByteBuf buf) {
		id = ByteBufUtils.readUTF8String(buf);
		isowner = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	ByteBufUtils.writeUTF8String(buf, id);
    	buf.writeBoolean(isowner);
    }

    public static class Handler implements IMessageHandler<SWalkmanStopPacket, IMessage> {
        @Override
        public IMessage onMessage(SWalkmanStopPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(SWalkmanStopPacket message, MessageContext ctx) {
        	audiocassettes.proxy.WalkmanStop(message.id,message.isowner);
        }
    }
}
