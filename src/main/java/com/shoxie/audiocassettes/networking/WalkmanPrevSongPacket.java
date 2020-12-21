package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.item.WalkmanItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class WalkmanPrevSongPacket implements IMessage {

    public WalkmanPrevSongPacket() { }
    
    @Override
    public void fromBytes(ByteBuf buf) { }

    @Override
    public void toBytes(ByteBuf buf) { }

    public static class Handler implements IMessageHandler<WalkmanPrevSongPacket, IMessage> {
        @Override
        public IMessage onMessage(WalkmanPrevSongPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(WalkmanPrevSongPacket message, MessageContext ctx) {
        	ItemStack mp = WalkmanItem.getMPInHand(ctx.getServerHandler().player);
        	WalkmanItem.switchSong(false, WalkmanItem.getCassette(mp));
            if(WalkmanItem.isPlaying(mp)) {
            	WalkmanItem.stopMusic(WalkmanItem.getID(mp),ctx.getServerHandler().player,false);
            	WalkmanItem.setPlaying(mp, false);
            }
        }
    }
}
