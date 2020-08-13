package com.shoxie.audiocassettes;

import com.shoxie.audiocassettes.tile.BoomBoxTile;
import com.shoxie.audiocassettes.tile.TapeDeckTile;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModTileEntities {

	@ObjectHolder("audiocassettes:tapedeck")
	public static TileEntityType<TapeDeckTile> TILE_TAPE_DECK;

	@ObjectHolder("audiocassettes:boombox")
	public static TileEntityType<BoomBoxTile> TILE_BOOMBOX;
}
