package com.shoxie.audiocassettes.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.shoxie.audiocassettes.ModContainers;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.audio.BoomBoxSound;
import com.shoxie.audiocassettes.audio.WalkmanSound;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.networking.BoomBoxNextSongPacket;
import com.shoxie.audiocassettes.networking.WalkmanNextSongPacket;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.screen.BoomBoxScreen;
import com.shoxie.audiocassettes.screen.WalkmanScreen;
import com.shoxie.audiocassettes.screen.TapeDeckScreen;
import com.shoxie.audiocassettes.tile.BoomBoxTile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {
	
	Map<String,BoomBoxSound> cpsounds = new HashMap<String,BoomBoxSound>();
	Map<String,WalkmanSound> Walkmansounds = new HashMap<String,WalkmanSound>();
	private static final Minecraft mc = Minecraft.getInstance();
	
	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
    public void WalkmanPlay(String id, String playerid,boolean isowner,SoundEvent snd, String songtitle) {
		Minecraft.getInstance().enqueue(() -> {        	
			if(mc.getSoundHandler().isPlaying(Walkmansounds.get(id))) return;
			ItemStack mp = ItemStack.EMPTY;
        	if(isowner) {
        		mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
        		WalkmanItem.setPlaying(mp, true);
        		WalkmanItem.setLoop(mp, true);
        	}
        	
        	PlayerEntity owner = isowner ? getClientPlayer() : getClientPlayer().world.getPlayerByUuid(UUID.fromString(playerid));
        	Walkmansounds.put(id,new WalkmanSound(owner, snd)); 
        	if(audiocassettes.announceenabled) mc.ingameGUI.setOverlayMessage(net.minecraftforge.common.ForgeHooks.newChatWithLinks(I18n.format("record.nowPlaying", songtitle)), true);
            mc.getSoundHandler().play((ISound) Walkmansounds.get(id));

            new Thread()
            {
            	
                @Override
                public void run()
                {
                	boolean isPlaying = false;
                	boolean isLoop = false;
                	ItemStack mp = ItemStack.EMPTY;
                	if(isowner) {
	                	mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
	                    if(mp.getItem() instanceof WalkmanItem) {
	                    	isPlaying = WalkmanItem.IsPlaying(mp);
	                    	isLoop = WalkmanItem.IsLoop(mp);
	                    }
                	}
                    while(mc.getSoundHandler().isPlaying(Walkmansounds.get(id))) {
                    	try {
    						Thread.sleep(1000);
    					} catch (InterruptedException e) {
    						System.out.println(e.getMessage());
    					}
                    }
                    
                	if(isowner) {
	                    mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
	                    if(mp.getItem() instanceof WalkmanItem) {
		                    WalkmanItem.setPlaying(mp, false); isPlaying = false;
		                    isLoop = WalkmanItem.IsLoop(mp);
	                    }
	                    
                    if(isLoop && getClientWorld() != null) 
                    	Networking.INSTANCE.sendToServer(new WalkmanNextSongPacket(id));
                	
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
		        WalkmanItem.setLoop(mp, false);
		        WalkmanItem.setPlaying(mp, false);
        	}
		}
		mc.getSoundHandler().stop(Walkmansounds.get(id));
    }
	
	
	
	@Override
	public void BoomBoxPlay(BlockPos pos, String id, boolean isowner, SoundEvent snd, String songtitle) {
        Minecraft.getInstance().enqueue(() -> {
        	if(mc.getSoundHandler().isPlaying(cpsounds.get(id))) return;
        	BoomBoxTile tile = (BoomBoxTile)getClientWorld().getTileEntity(pos);
        	cpsounds.put(id,new BoomBoxSound(snd,pos.getX(),pos.getY(),pos.getZ(),getClientPlayer()));
        	tile.id = id;
            if (!tile.isPlaying && !mc.getSoundHandler().isPlaying(cpsounds.get(id))) {
            	if(!tile.isLoop) tile.isLoop=true;
            	if(audiocassettes.announceenabled) mc.ingameGUI.setOverlayMessage(net.minecraftforge.common.ForgeHooks.newChatWithLinks(I18n.format("record.nowPlaying", songtitle)), true);
                mc.getSoundHandler().play((ISound) cpsounds.get(id));
                tile.isPlaying = true;
            }

            new Thread()
            {
                @Override
                public void run()
                {
                    while(mc.getSoundHandler().isPlaying(cpsounds.get(id))) {
                    	try {
    						Thread.sleep(1000);
    					} catch (InterruptedException e) {
    						System.out.println(e.getMessage());
    					}
                    }

                    tile.isPlaying = false;
                    if(tile.isLoop && getClientWorld() != null && isowner) {
                    	Networking.INSTANCE.sendToServer(new BoomBoxNextSongPacket(tile.getPos(), false));
                    }
                }
            }.start();
            
        });
    }

	@Override
	public void BoomBoxStop(BlockPos pos, String id) {
        Minecraft.getInstance().enqueue(() -> {
        	BoomBoxTile tile = (BoomBoxTile)getClientWorld().getTileEntity(pos);
            mc.getSoundHandler().stop(cpsounds.get(id));
            if(tile != null) {
		        tile.isLoop = false;
		        tile.isPlaying = false;
            }
        });
	}
	
	@Override
	public World getClientWorld() {
		return Minecraft.getInstance().world;
	}

	@Override
	public boolean isBoomBoxPlaying(String id){
    	if(mc.getSoundHandler().isPlaying(cpsounds.get(id))) return true;
    	return false;
		
	}
	
	@Override
	public boolean isWalkmanPlaying(String id){
    	if(mc.getSoundHandler().isPlaying(Walkmansounds.get(id))) return true;
    	return false;
		
	}

	@Override
	public void ScreenInit() {
		ScreenManager.registerFactory(ModContainers.CONTAINER_TAPE_DECK, TapeDeckScreen::new);
		ScreenManager.registerFactory(ModContainers.CONTAINER_BOOM_BOX, BoomBoxScreen::new);
		ScreenManager.registerFactory(ModContainers.CONTAINER_WALKMAN, WalkmanScreen::new);
	}
}