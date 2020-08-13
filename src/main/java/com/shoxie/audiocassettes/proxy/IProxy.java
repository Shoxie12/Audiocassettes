package com.shoxie.audiocassettes.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IProxy {
	
	PlayerEntity getClientPlayer();

	World getClientWorld();

	void ScreenInit();
	
	//Walkman
	
	void WalkmanPlay(String id, String playerid,boolean isowner,SoundEvent snd, String songtitle);

	void WalkmanStop(String id, boolean isowner);
	
	boolean isWalkmanPlaying(String id);
	
	//BoomBox
	
	void BoomBoxPlay(BlockPos pos, String id, boolean isowner, SoundEvent snd, String songtitle);
	
	void BoomBoxStop(BlockPos pos, String id);
	
	boolean isBoomBoxPlaying(String id);
	
	
	
}
