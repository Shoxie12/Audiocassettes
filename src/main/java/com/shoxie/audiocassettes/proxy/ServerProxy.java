package com.shoxie.audiocassettes.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {
	
	@Override
	public PlayerEntity getClientPlayer() {
		return null;
	}
	
	@Override
	public World getClientWorld() {
		return null;
	}

	@Override
	public void ScreenInit() {
	}

	@Override
	public void WalkmanStop(String id, boolean isowner) {
	}

	@Override
	public void WalkmanPlay(String id, String playerid,boolean isowner,SoundEvent snd, String songtitle) {
	} 
	
	@Override
	public void BoomBoxPlay(BlockPos pos, String id,boolean isowner, SoundEvent snd, String songtitle) {
	}

	@Override
	public void BoomBoxStop(BlockPos pos, String id) {
	}
	
	@Override
	public boolean isBoomBoxPlaying(String id){
    	return false;
	}
	
	@Override
	public boolean isWalkmanPlaying(String id){
    	return false;
	}
}