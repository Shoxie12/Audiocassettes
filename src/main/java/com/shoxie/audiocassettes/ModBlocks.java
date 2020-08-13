package com.shoxie.audiocassettes;

import com.shoxie.audiocassettes.block.BoomBoxBlock;
import com.shoxie.audiocassettes.block.TapeDeckBlock;

import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {
	
	@ObjectHolder("audiocassettes:tapedeck")
	public static TapeDeckBlock TAPE_DECK = new TapeDeckBlock();
	
	@ObjectHolder("audiocassettes:boombox")
	public static BoomBoxBlock BOOMBOX = new BoomBoxBlock();
}
