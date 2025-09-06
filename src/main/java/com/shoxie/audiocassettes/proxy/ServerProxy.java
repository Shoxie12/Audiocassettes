package com.shoxie.audiocassettes.proxy;


import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ServerProxy implements IProxy {

    @Override
    public Minecraft getMinecraft() {
        return null;
    }

    @Override
	public Player getClientPlayer() {
		return null;
	}
	
	@Override
	public Level getClientLevel() {
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
	public void BoomBoxPlay(BlockPos pos, String id, boolean isowner, SoundEvent snd, String songtitle) {
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