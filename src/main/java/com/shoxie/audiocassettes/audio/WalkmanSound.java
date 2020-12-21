package com.shoxie.audiocassettes.audio;

import com.shoxie.audiocassettes.audiocassettes;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class WalkmanSound extends TickableSound {
    private final PlayerEntity player;
    private final PlayerEntity mpowner;

    public WalkmanSound(PlayerEntity mpowner, SoundEvent event) {
        super(event, SoundCategory.RECORDS);
        this.player = audiocassettes.proxy.getClientPlayer();
		this.mpowner = mpowner;
        this.repeat = false;
        this.repeatDelay = 0;
    }

    public void tick() {
        if (!this.player.isAlive()) 
        	this.finishPlaying();
        else {
            this.x = (float) this.player.getPosX();
            this.y = (float) this.player.getPosY();
            this.z = (float) this.player.getPosZ();
    		float _x = (float) Math.abs(this.mpowner.getPosX() - this.x);
    		float _y = (float) Math.abs(this.mpowner.getPosY() - this.y);
    		float _z = (float) Math.abs(this.mpowner.getPosZ() - this.z);
    		int m = audiocassettes.WalkmanMaxSoundDistance;
    		if(_x < m && _y < m && _z < m) this.volume = 1.0F;
    		else if(volcalc(_x) <= 0.0F || volcalc(_y) <= 0.0F || volcalc(_z) <= 0.0F) this.volume = 0.0F;
    		else this.volume = _x >= m ? volcalc(_x) : _y >= m ?  volcalc(_y) : _z >= m ?  volcalc(_z) : 0.0F; 
    		
    	}
    }
    
    private float volcalc(float x) {
    	return 1.0F - ((x-audiocassettes.WalkmanMaxSoundDistance)/10);
    }
}
