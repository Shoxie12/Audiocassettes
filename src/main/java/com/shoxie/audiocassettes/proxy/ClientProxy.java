package com.shoxie.audiocassettes.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.shoxie.audiocassettes.ModConfig;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.audio.BoomBoxSound;
import com.shoxie.audiocassettes.audio.WalkmanSound;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.networking.BoomBoxNextSongPacket;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.networking.WalkmanNextSongPacket;
import com.shoxie.audiocassettes.tile.TileBoomBox;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	
	Map<String,BoomBoxSound> cpsounds = new HashMap<String,BoomBoxSound>();
	Map<String,WalkmanSound> Walkmansounds = new HashMap<String,WalkmanSound>();
	private static final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        OBJLoader.INSTANCE.addDomain(audiocassettes.MODID);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
	
	@Override
	public void renderItem(Item item, int meta, ResourceLocation rl) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(rl, "inventory"));
	}
	
	@Override
    public void renderBlock(Block block) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(new ItemBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
	
	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}

	@Override
    public void WalkmanPlay(String id, String playerid,boolean isowner,SoundEvent snd, String songtitle) {
			if(mc.getSoundHandler().isSoundPlaying(Walkmansounds.get(id))) return;
			ItemStack mp = ItemStack.EMPTY;
        	if(isowner) {
        		mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
        		WalkmanItem.setPlaying(mp, true);
        	}
        	
        	EntityPlayer owner = isowner ? getClientPlayer() : getClientPlayer().world.getPlayerEntityByUUID(UUID.fromString(playerid));
        	Walkmansounds.put(id,new WalkmanSound(owner, snd)); 
            if(ModConfig.nowplayingannounce) mc.ingameGUI.setRecordPlayingMessage(songtitle);
            mc.getSoundHandler().playSound((ISound) Walkmansounds.get(id));

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
                    while(mc.getSoundHandler().isSoundPlaying(Walkmansounds.get(id))) {
                    	try {
    						Thread.sleep(1000);
    					} catch (InterruptedException e) {
    						System.out.println(e.getMessage());
    					}
                    }
                    
                	if(isowner) {
	                    mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
	                    if(mp.getItem() instanceof WalkmanItem) 
		                    isPlaying = WalkmanItem.isPlaying(mp);
	                    
	                    
	                    if(isPlaying && getClientWorld() != null) 
	                    	Networking.INSTANCE.sendToServer(new WalkmanNextSongPacket(id));
                    	
                	}
                }
            }.start();
    }
	
	@Override
    public void WalkmanStop(String id, boolean isowner) {
		if(isowner) {
			ItemStack mp = WalkmanItem.getMPbyID(audiocassettes.proxy.getClientPlayer(), id);
        	if(mp.getItem() instanceof WalkmanItem) {
		        WalkmanItem.setPlaying(mp, false);
        	}
		}
		mc.getSoundHandler().stopSound(Walkmansounds.get(id));
    }
	
	
	
	@Override
	public void BoomBoxPlay(BlockPos pos, String id, boolean isowner, SoundEvent snd, String songtitle) {
        	if(mc.getSoundHandler().isSoundPlaying(cpsounds.get(id))) return;
        	TileBoomBox tile = (TileBoomBox)getClientWorld().getTileEntity(pos);
        	cpsounds.put(id,new BoomBoxSound(snd,pos.getX(),pos.getY(),pos.getZ()));
        	tile.id = id;

        	if (!mc.getSoundHandler().isSoundPlaying(cpsounds.get(id))) {
        		tile.isPlaying=true;
            	if(ModConfig.nowplayingannounce) mc.ingameGUI.setRecordPlayingMessage(songtitle);
                mc.getSoundHandler().playSound((ISound) cpsounds.get(id));
            }

            new Thread()
            {
                @Override
                public void run()
                {
                    while(mc.getSoundHandler().isSoundPlaying(cpsounds.get(id))) {
                    	try {
    						Thread.sleep(1000);
    					} catch (InterruptedException e) {
    						System.out.println(e.getMessage());
    					}
                    }

                    if(tile.isPlaying && getClientWorld() != null && isowner) {
                    	Networking.INSTANCE.sendToServer(new BoomBoxNextSongPacket(tile.getPos(), false));
                    }
                }
            }.start();
    }

	@Override
	public void BoomBoxStop(BlockPos pos, String id) {
        	TileBoomBox tile = (TileBoomBox)getClientWorld().getTileEntity(pos);
            mc.getSoundHandler().stopSound(cpsounds.get(id));
            if(tile != null)
		        tile.isPlaying = false;
	}
	
	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().world;
	}

	@Override
	public boolean isBoomBoxPlaying(String id){
    	if(mc.getSoundHandler().isSoundPlaying(cpsounds.get(id))) return true;
    	return false;
		
	}
	
	@Override
	public boolean isWalkmanPlaying(String id){
    	if(mc.getSoundHandler().isSoundPlaying(Walkmansounds.get(id))) return true;
    	return false;
		
	}
}