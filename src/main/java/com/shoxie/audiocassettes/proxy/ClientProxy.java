package com.shoxie.audiocassettes.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.audio.BoomBoxSound;
import com.shoxie.audiocassettes.audio.WalkmanSound;
import com.shoxie.audiocassettes.gui.ConfigScreen;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.networking.BoomBoxNextSongPacket;
import com.shoxie.audiocassettes.networking.WalkmanNextSongPacket;
import com.shoxie.audiocassettes.networking.WalkmanOnDropPacket;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.entity.BoomBoxEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

public class ClientProxy implements IProxy {
	
	Map<String,BoomBoxSound> cpsounds = new HashMap<String,BoomBoxSound>();
	Map<String,WalkmanSound> Walkmansounds = new HashMap<String,WalkmanSound>();

    @Override
    public Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    @Override
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
    public void WalkmanPlay(String id, String playerid, boolean isowner, SoundEvent snd, String songtitle) {
        Minecraft mc = getMinecraft();

		Minecraft.getInstance().execute(() -> {
			if(mc.getSoundManager().isActive(Walkmansounds.get(id))) return;
			ItemStack mp;
        	if(isowner) {
        		mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
        		WalkmanItem.setPlaying(mp, true);
        	}
        	
        	Player owner = isowner ? getClientPlayer() : getClientLevel().getPlayerByUUID(UUID.fromString(playerid));
        	Walkmansounds.put(id,new WalkmanSound(owner, snd));
        	if(audiocassettes.announceenabled) 
        		mc.gui.setOverlayMessage(net.minecraftforge.common.ForgeHooks.newChatWithLinks(Component.translatable("record.nowPlaying", songtitle).getString()), true);
            mc.getSoundManager().play(Walkmansounds.get(id));

            new Thread()
            {
            	
                @Override
                public void run()
                {
                	boolean isPlaying = false;
                	ItemStack mp;
                	if(isowner) {
	                	mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
	                    if(mp.getItem() instanceof WalkmanItem) 
	                    	isPlaying = WalkmanItem.isPlaying(mp);
                	}
                	boolean presentMP = isowner && WalkmanItem.isPlayerOwnMp(getClientPlayer(),id);
                    while(mc.getSoundManager().isActive(Walkmansounds.get(id)) && presentMP) {
                    	try {
    						Thread.sleep(1000);
    						presentMP = WalkmanItem.isPlayerOwnMp(getClientPlayer(),id);
    					} catch (InterruptedException e) {
    						audiocassettes.logger.warn(e.getMessage());
    					}
                    }
                	if(isowner) {
                		if(presentMP) {
		                    mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
		                    if(mp.getItem() instanceof WalkmanItem) 
			                    isPlaying = WalkmanItem.isPlaying(mp);
		                    
		                    if(isPlaying && getClientLevel() != null) 
		                    	Networking.INSTANCE.sendToServer(new WalkmanNextSongPacket(id));
	                	}
                		else Networking.INSTANCE.sendToServer(new WalkmanOnDropPacket(id));
                	}
                }
            }.start();
		});
    }
	
	@Override
    public void WalkmanStop(String id, boolean isowner) {
		if(isowner) {
			ItemStack mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
        	if(mp.getItem() instanceof WalkmanItem) {
		        WalkmanItem.setPlaying(mp, false);
        	}
		}
		getMinecraft().getSoundManager().stop(Walkmansounds.get(id));
    }
	
	
	
	@Override
	public void BoomBoxPlay(BlockPos pos, String id, boolean isowner, SoundEvent snd, String songtitle) {
        Minecraft mc = getMinecraft();

        Minecraft.getInstance().execute(() -> {
        	if(mc.getSoundManager().isActive(cpsounds.get(id))) return;
        	BoomBoxEntity entity = (BoomBoxEntity)getClientLevel().getBlockEntity(pos);
        	cpsounds.put(id,new BoomBoxSound(snd,pos.getX(),pos.getY(),pos.getZ(),getClientPlayer()));
        	entity.id = id;
        	if (!mc.getSoundManager().isActive(cpsounds.get(id))) {
        		entity.isPlaying=true;
            	if(audiocassettes.announceenabled) 
            		mc.gui.setOverlayMessage(net.minecraftforge.common.ForgeHooks.newChatWithLinks(Component.translatable("record.nowPlaying", songtitle).getString()), true);
                mc.getSoundManager().play(cpsounds.get(id));
            }

            new Thread()
            {
                @Override
                public void run()
                {
                    while(mc.getSoundManager().isActive(cpsounds.get(id))) {
                    	try {
    						Thread.sleep(1000);
    					} catch (InterruptedException e) {
    						audiocassettes.logger.warn(e.getMessage());
    					}
                    }

                    if(!entity.isRemoved() && entity.isPlaying && getClientLevel() != null && isowner) {
                    	Networking.INSTANCE.sendToServer(new BoomBoxNextSongPacket(entity.getBlockPos(), false));
                    }
                }
            }.start();
            
        });
    }

	@Override
	public void BoomBoxStop(BlockPos pos, String id) {
        Minecraft.getInstance().execute(() -> {
        	BoomBoxEntity entity = (BoomBoxEntity)getClientLevel().getBlockEntity(pos);
            getMinecraft().getSoundManager().stop(cpsounds.get(id));
            if(entity != null)
		        entity.isPlaying = false;
        });
	}
	
	@Override
	public Level getClientLevel() {
		return Minecraft.getInstance().level;
	}

	@Override
	public boolean isBoomBoxPlaying(String id){
        return getMinecraft().getSoundManager().isActive(cpsounds.get(id));

    }
	
	@Override
	public boolean isWalkmanPlaying(String id){
        return getMinecraft().getSoundManager().isActive(Walkmansounds.get(id));

    }

	@Override
	public void ScreenInit() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ConfigScreen(screen,mc.options)));
	}
}