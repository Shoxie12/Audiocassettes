package com.shoxie.audiocassettes.proxy;


import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;

public interface IProxy {

    Minecraft getMinecraft();

	Player getClientPlayer();

	Level getClientLevel();

	void ScreenInit();
	
	//Walkman
	
	void WalkmanPlay(String id, String playerid, boolean isowner, SoundEvent snd, String songtitle);

	void WalkmanStop(String id, boolean isowner);
	
	boolean isWalkmanPlaying(String id);
	
	//BoomBox
	
	void BoomBoxPlay(BlockPos pos, String id, boolean isowner, SoundEvent snd, String songtitle);
	
	void BoomBoxStop(BlockPos pos, String id);
	
	boolean isBoomBoxPlaying(String id);
	
	
	
}
