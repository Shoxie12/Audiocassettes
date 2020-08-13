package com.shoxie.audiocassettes.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class MagneticTapeItem extends Item{
	private static String name = "magnetictape";
	public MagneticTapeItem() {
		super(new Item.Properties().group(ItemGroup.MISC));
		setRegistryName(name);
	}

}
