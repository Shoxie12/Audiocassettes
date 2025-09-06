package com.shoxie.audiocassettes.audio;

import com.shoxie.audiocassettes.audiocassettes;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public class WalkmanSound extends AbstractSoundInstance implements TickableSoundInstance {
    private final Player player;
    private final Player mpowner;

    public WalkmanSound(Player mpowner, SoundEvent event) {
        super(event.getLocation(), SoundSource.RECORDS, RandomSource.create() );
        this.player = audiocassettes.proxy.getClientPlayer();
		this.mpowner = mpowner;
    }

    @Override
    public boolean isStopped() {
        return !Minecraft.getInstance().getSoundManager().isActive(this);
    }

    @Override
    public void tick() {
        if (!this.player.isAlive())
            Minecraft.getInstance().getSoundManager().stop(this.getLocation(),this.getSource());
        else {
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
    		float _x = (float) Math.abs(this.mpowner.getX() - this.x);
    		float _y = (float) Math.abs(this.mpowner.getY() - this.y);
    		float _z = (float) Math.abs(this.mpowner.getZ() - this.z);
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
