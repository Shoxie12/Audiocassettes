package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.item.WalkmanItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CWalkmanPlayPacket implements IMessage {
	
	private String id;
    
	public CWalkmanPlayPacket() { }
	
    public CWalkmanPlayPacket(String id) {
        this.id = id;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	id = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	ByteBufUtils.writeUTF8String(buf, id);
    }

    public static class Handler implements IMessageHandler<CWalkmanPlayPacket, IMessage> {
        @Override
        public IMessage onMessage(CWalkmanPlayPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(CWalkmanPlayPacket message, MessageContext ctx) {
            EntityPlayerMP playerMP = ctx.getServerHandler().player;
        	ItemStack mp = ItemStack.EMPTY;
        	if(message.id.equals("-")) mp = WalkmanItem.getMPInHand(playerMP);
        	else mp = WalkmanItem.getMPbyID(playerMP, message.id);
        	if(mp != ItemStack.EMPTY) {
        		WalkmanItem.playMusic(mp, playerMP.getServerWorld(), playerMP);
        	}
        }
    }
}
