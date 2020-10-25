package com.shoxie.audiocassettes.audio;

import com.shoxie.audiocassettes.audiocassettes;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class WalkmanSound extends PositionedSound implements ITickableSound {
	private boolean donePlaying;
	private EntityPlayer mpowner;

    public WalkmanSound(EntityPlayer mpowner, SoundEvent event) {
        super(event, SoundCategory.RECORDS);
		this.mpowner = mpowner;
        this.repeat = false;
        this.repeatDelay = 0;
        this.volume = audiocassettes.WalkmanMaxSoundDistance * 0.0625F;
    }
    
	@Override
	public float getXPosF() {
		return this.xPosF;
	}
	
	@Override
	public float getYPosF() {
		return this.yPosF;
	}
	
	@Override
	public float getZPosF() {
		return this.zPosF;
	}

	@Override
	public void update() {
        if (!this.mpowner.isEntityAlive()) 
            this.donePlaying = true;
        else {
	            this.xPosF = (float) this.mpowner.getPosition().getX();
	            this.yPosF = (float) this.mpowner.getPosition().getY();
	            this.zPosF = (float) this.mpowner.getPosition().getZ();
        }
        
	}

	@Override
	public boolean isDonePlaying() {
		return this.donePlaying;
	}
}
