package com.shoxie.audiocassettes.audio;

import com.shoxie.audiocassettes.audiocassettes;

import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class BoomBoxSound extends PositionedSound {
	public BoomBoxSound(SoundEvent event, int x, int y, int z) {
        super(event, SoundCategory.RECORDS);
        this.volume = audiocassettes.BoomBoxMaxSoundDistance * 0.0625F;
        this.repeat = false;
        this.repeatDelay = 0;
        this.xPosF = x;
        this.yPosF = y;
        this.zPosF = z;
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
}
