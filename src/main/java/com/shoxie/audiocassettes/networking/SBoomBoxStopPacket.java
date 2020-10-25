package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.audiocassettes;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SBoomBoxStopPacket implements IMessage {
	
    private BlockPos pos;
    private String ID;
    
    public SBoomBoxStopPacket() { }
    
    public SBoomBoxStopPacket(BlockPos pos, String ID) {
        this.pos = pos;
        this.ID = ID;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	pos = BlockPos.fromLong(buf.readLong());
    	ID = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, ID);
    }

    public static class Handler implements IMessageHandler<SBoomBoxStopPacket, IMessage> {
        @Override
        public IMessage onMessage(SBoomBoxStopPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(SBoomBoxStopPacket message, MessageContext ctx) {            
        	audiocassettes.proxy.BoomBoxStop(message.pos, message.ID);
        }
    }
}
