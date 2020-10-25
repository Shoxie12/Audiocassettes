package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SWalkmanPlayPacket implements IMessage {
	
    private String id;
    private String playerid;
    private boolean isowner;
    private ItemStack cassette;
    
    public SWalkmanPlayPacket() { }
    
    public SWalkmanPlayPacket(String id, String playerid, boolean isowner, ItemStack cassette) {
		this.id = id;
		this.playerid = playerid;
		this.isowner = isowner;
		this.cassette = cassette;
	}

	@Override
    public void fromBytes(ByteBuf buf) {
		id = ByteBufUtils.readUTF8String(buf);
		playerid = ByteBufUtils.readUTF8String(buf);
		isowner = buf.readBoolean();
		cassette = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	ByteBufUtils.writeUTF8String(buf, id);
    	ByteBufUtils.writeUTF8String(buf, playerid);
    	buf.writeBoolean(isowner);
    	ByteBufUtils.writeItemStack(buf, cassette);
    }

    public static class Handler implements IMessageHandler<SWalkmanPlayPacket, IMessage> {
        @Override
        public IMessage onMessage(SWalkmanPlayPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(SWalkmanPlayPacket message, MessageContext ctx) {
        	if(message.cassette.getItem() instanceof AbstractAudioCassetteItem)
        		audiocassettes.proxy.WalkmanPlay(message.id,message.playerid,message.isowner,
        				AbstractAudioCassetteItem.getCurrentSong(message.cassette),
        				AbstractAudioCassetteItem.getSongTitle(message.cassette)
        	);
        }
    }
}
