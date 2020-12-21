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

public class WalkmanNextSongPacket implements IMessage {

	private String id;
	
	public WalkmanNextSongPacket() { }
	
	public WalkmanNextSongPacket(String id) { 
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

    public static class Handler implements IMessageHandler<WalkmanNextSongPacket, IMessage> {
        @Override
        public IMessage onMessage(WalkmanNextSongPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(WalkmanNextSongPacket message, MessageContext ctx) {
	    	ItemStack mp = ItemStack.EMPTY;
	    	EntityPlayerMP playerMP = ctx.getServerHandler().player;
	    	if(message.id.equals("-")) {
	    		mp = WalkmanItem.getMPInHand(playerMP); 
	    		if(WalkmanItem.isPlaying(mp)) {
		            WalkmanItem.stopMusic(WalkmanItem.getID(mp),playerMP,false);
		    		WalkmanItem.setPlaying(mp, false);
	    		}
	    	}
	    	else mp = WalkmanItem.getMPbyID(playerMP, message.id);
	    	if(mp == ItemStack.EMPTY) return;
            
	    	boolean switched = WalkmanItem.switchSong(true, WalkmanItem.getCassette(mp));
	        if(!(message.id.equals("-")) && switched) WalkmanItem.playMusic(mp, playerMP.getServerWorld(), playerMP);
        }
    }
}