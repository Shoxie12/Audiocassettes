package com.shoxie.audiocassettes.item;

import net.minecraft.creativetab.CreativeTabs;

public class DiamondAudioCassetteItem extends AbstractAudioCassetteItem{

	private static String name = "diamondaudiocassette";
	public DiamondAudioCassetteItem() {
		this.maxslots = 25;
		this.MaxWriteTime = 50;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTabs.MISC);
		this.maxStackSize = 1;
	}
}
