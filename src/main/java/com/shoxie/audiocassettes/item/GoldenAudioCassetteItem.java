package com.shoxie.audiocassettes.item;

import net.minecraft.creativetab.CreativeTabs;

public class GoldenAudioCassetteItem extends AbstractAudioCassetteItem{

	private static String name = "goldenaudiocassette";
	public GoldenAudioCassetteItem() {
		this.maxslots = 16;
		this.MaxWriteTime = 100;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTabs.MISC);
		this.maxStackSize = 1;
	}
}
