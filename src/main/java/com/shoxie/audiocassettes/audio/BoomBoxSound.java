package com.shoxie.audiocassettes.audio;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;


public class BoomBoxSound extends AbstractSoundInstance {
    private final SoundEvent snd;


    public BoomBoxSound(SoundEvent snd, int _x, int _y, int _z, Player player) {
        super(snd, SoundSource.RECORDS, RandomSource.create());
        this.snd = snd;
        this.snd.getRange(5);
        this.x = _x;
        this.y = _y;
        this.z = _z;
    }
}
