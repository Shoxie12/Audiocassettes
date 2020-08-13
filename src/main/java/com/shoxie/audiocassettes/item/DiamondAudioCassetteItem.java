package com.shoxie.audiocassettes.item;

public class DiamondAudioCassetteItem extends AbstractAudioCassetteItem{

	public DiamondAudioCassetteItem() {
		super();
		this.name = "diamondaudiocassette";
		this.maxslots = 25;
		this.MaxWriteTime = 50;
		setRegistryName(name);
	}
}
