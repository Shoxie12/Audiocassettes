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
import com.shoxie.audiocassettes.networking.WalkmanOnDropPacket;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.screen.BoomBoxScreen;
import com.shoxie.audiocassettes.screen.WalkmanScreen;
import com.shoxie.audiocassettes.screen.TapeDeckScreen;
import com.shoxie.audiocassettes.tile.BoomBoxTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.ScreenManager;
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
        	}
        	
        	PlayerEntity owner = isowner ? getClientPlayer() : getClientWorld().getPlayerByUuid(UUID.fromString(playerid));
        	Walkmansounds.put(id,new WalkmanSound(owner, snd)); 
            if(audiocassettes.announceenabled) mc.ingameGUI.setRecordPlayingMessage(songtitle);
            mc.getSoundHandler().play((ISound) Walkmansounds.get(id));

            new Thread()
            {
            	
                @Override
                public void run()
                {
                	boolean isPlaying = false;
                	ItemStack mp = ItemStack.EMPTY;
                	if(isowner) {
	                	mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
	                    if(mp.getItem() instanceof WalkmanItem) 
	                    	isPlaying = WalkmanItem.isPlaying(mp);
                	}
                	boolean presentMP = isowner && WalkmanItem.isPlayerOwnMp(getClientPlayer(),id);
                    while(mc.getSoundHandler().isPlaying(Walkmansounds.get(id)) && presentMP) {
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
		                    
		                    if(isPlaying && getClientWorld() != null) 
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
		mc.getSoundHandler().stop(Walkmansounds.get(id));
    }
	
	
	
	@Override
	public void BoomBoxPlay(BlockPos pos, String id, boolean isowner, SoundEvent snd, String songtitle) {
        Minecraft.getInstance().enqueue(() -> {
        	if(mc.getSoundHandler().isPlaying(cpsounds.get(id))) return;
        	BoomBoxTile tile = (BoomBoxTile)getClientWorld().getTileEntity(pos);
        	cpsounds.put(id,new BoomBoxSound(snd,pos.getX(),pos.getY(),pos.getZ(),getClientPlayer()));
        	tile.id = id;
        	if (!mc.getSoundHandler().isPlaying(cpsounds.get(id))) {
        		tile.isPlaying=true;
            	if(audiocassettes.announceenabled) mc.ingameGUI.setRecordPlayingMessage(songtitle);
                mc.getSoundHandler().play((ISound) cpsounds.get(id));
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
    						audiocassettes.logger.warn(e.getMessage());
    					}
                    }

                    if(!tile.isRemoved() && tile.isPlaying && getClientWorld() != null && isowner) {
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
            if(tile != null)
		        tile.isPlaying = false;
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