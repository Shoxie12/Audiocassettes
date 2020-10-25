package com.shoxie.audiocassettes.item;

import net.minecraft.creativetab.CreativeTabs;

public class IronAudioCassetteItem extends AbstractAudioCassetteItem{

	private static String name = "ironaudiocassette";
	public IronAudioCassetteItem() {
		this.maxslots = 10;
		this.MaxWriteTime = 150;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTabs.MISC);
		this.maxStackSize = 1;
	}
}
