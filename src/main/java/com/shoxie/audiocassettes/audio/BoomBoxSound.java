package com.shoxie.audiocassettes.audio;

import com.shoxie.audiocassettes.audiocassettes;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class BoomBoxSound extends TickableSound {
    private PlayerEntity player;

	public BoomBoxSound(SoundEvent event, int x, int y, int z, PlayerEntity player) {
        super(event, SoundCategory.RECORDS);
        this.volume = 1.0F;
        this.repeat = false;
        this.player = player;
        this.repeatDelay = 0;
        this.x = x;
        this.y = y;
        this.z = z;
    }

	@Override
	public void tick() {
		float _x = (float) Math.abs(player.getPosX() - this.x);
		float _y = (float) Math.abs(player.getPosY() - this.y);
		float _z = (float) Math.abs(player.getPosZ() - this.z);
		int m = audiocassettes.BoomBoxMaxSoundDistance;
		if(_x < m && _y < m && _z < m) this.volume = 1.0F;
		else if(volcalc(_x) <= 0.0F || volcalc(_y) <= 0.0F || volcalc(_z) <= 0.0F) this.volume = 0.0F;
		else this.volume = _x >= m ? volcalc(_x) : _y >= m ?  volcalc(_y) : _z >= m ?  volcalc(_z) : 0.0F; 
		
	}
	
	private float volcalc(float x) {
		return 1.0F - ((x-audiocassettes.BoomBoxMaxSoundDistance)/10);
	}
}
