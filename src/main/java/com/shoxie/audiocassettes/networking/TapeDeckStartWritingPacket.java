package com.shoxie.audiocassettes.networking;

import com.shoxie.audiocassettes.tile.TileTapeDeck;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TapeDeckStartWritingPacket implements IMessage {
	
    private BlockPos pos;
    private String song;
    private String sname;

    public TapeDeckStartWritingPacket() { }
    
    public TapeDeckStartWritingPacket(BlockPos pos, ResourceLocation res, String sname) {
        this.pos = pos;
        String song = (res.getResourceDomain()+":"+res.getResourcePath());
        this.song = song.length() > 127 ? "Untitled" : song;
        this.sname = sname.length() > 127 ? "Untitled" : sname;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	pos = BlockPos.fromLong(buf.readLong());
    	song = ByteBufUtils.readUTF8String(buf);
    	sname = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, song);
        ByteBufUtils.writeUTF8String(buf, sname);
    }

    public static class Handler implements IMessageHandler<TapeDeckStartWritingPacket, IMessage> {
        @Override
        public IMessage onMessage(TapeDeckStartWritingPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(TapeDeckStartWritingPacket message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.getEntityWorld();
            if (world.isBlockLoaded(message.pos)) {
                TileTapeDeck tile = (TileTapeDeck)world.getTileEntity(message.pos);
                tile.StartWrite(message.song,message.sname);
            }
        }
    }
}
