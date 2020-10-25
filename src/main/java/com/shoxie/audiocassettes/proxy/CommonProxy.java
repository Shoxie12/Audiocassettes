package com.shoxie.audiocassettes.proxy;

import com.shoxie.audiocassettes.audiocassettes;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

    public static Configuration config;

    public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(audiocassettes.instance, new GuiProxy());
    }

    public void postInit(FMLPostInitializationEvent e) {

    }
	
	public void renderItem(Item item, int meta, ResourceLocation rl) {
	}
	
    public void renderBlock(Block block) {
    }
	
	public EntityPlayer getClientPlayer() {
		return null;
	}
	
	public World getClientWorld() {
		return null;
	}

	public void WalkmanStop(String id, boolean isowner) {
	}

	public void WalkmanPlay(String id, String playerid,boolean isowner,SoundEvent snd, String songtitle) {
	} 
	
	public void BoomBoxPlay(BlockPos pos, String id,boolean isowner, SoundEvent snd, String songtitle) {
	}

	public void BoomBoxStop(BlockPos pos, String id) {
	}
	
	public boolean isBoomBoxPlaying(String id){
    	return false;
	}
	
	public boolean isWalkmanPlaying(String id){
    	return false;
	}
}