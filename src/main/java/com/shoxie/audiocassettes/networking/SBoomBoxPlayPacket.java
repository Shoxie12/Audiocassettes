package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.item.AbstractAudioCassetteItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SBoomBoxPlayPacket implements IMessage {
	
    private BlockPos pos;
    private String ID;
    private boolean isowner;
    private ItemStack cassette;

    public SBoomBoxPlayPacket() { }
    
    public SBoomBoxPlayPacket(BlockPos pos, String ID, boolean isowner, ItemStack cassette) {
        this.pos = pos;
        this.ID = ID;
        this.isowner = isowner;
        this.cassette = cassette;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	pos = BlockPos.fromLong(buf.readLong());
    	ID = ByteBufUtils.readUTF8String(buf);
    	isowner = buf.readBoolean();
    	cassette = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, ID);
        buf.writeBoolean(isowner);
        ByteBufUtils.writeItemStack(buf, cassette);
    }

    public static class Handler implements IMessageHandler<SBoomBoxPlayPacket, IMessage> {
        @Override
        public IMessage onMessage(SBoomBoxPlayPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(SBoomBoxPlayPacket message, MessageContext ctx) {
        	if(message.cassette.getItem() instanceof AbstractAudioCassetteItem)
        		audiocassettes.proxy.BoomBoxPlay(message.pos,message.ID,message.isowner,
        				AbstractAudioCassetteItem.getCurrentSong(message.cassette),
        				AbstractAudioCassetteItem.getSongTitle(message.cassette)
        	);
        }
    }
}
