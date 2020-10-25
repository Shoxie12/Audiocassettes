package com.shoxie.audiocassettes.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class MagneticTapeItem extends Item{
	private static String name = "magnetictape";
	public MagneticTapeItem() {
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTabs.MISC);
	}

}
