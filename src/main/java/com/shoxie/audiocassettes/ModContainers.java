package com.shoxie.audiocassettes;

import com.shoxie.audiocassettes.container.BoomBoxContainer;
import com.shoxie.audiocassettes.container.WalkmanContainer;
import com.shoxie.audiocassettes.container.TapeDeckContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;

public class ModContainers {
	@ObjectHolder("audiocassettes:tapedeck")
    public static ContainerType<TapeDeckContainer> CONTAINER_TAPE_DECK;
	
	@ObjectHolder("audiocassettes:boombox")
    public static ContainerType<BoomBoxContainer> CONTAINER_BOOM_BOX;
	
	@ObjectHolder("audiocassettes:walkman")
    public static ContainerType<WalkmanContainer> CONTAINER_WALKMAN;
}
