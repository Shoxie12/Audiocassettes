package com.shoxie.audiocassettes.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class CassetteFrameItem extends Item{

	private static String name = "cassetteframe";
	public CassetteFrameItem() {
		super(new Item.Properties().group(ItemGroup.MISC));
		setRegistryName(name);
	}

}
