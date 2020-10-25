package com.shoxie.audiocassettes.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CassetteFrameItem extends Item{

	private static String name = "cassetteframe";
	public CassetteFrameItem() {
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTabs.MISC);
	}

}
