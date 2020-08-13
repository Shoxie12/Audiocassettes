package com.shoxie.audiocassettes.item;

public class GoldenAudioCassetteItem extends AbstractAudioCassetteItem{

	public GoldenAudioCassetteItem() {
		super();
		this.name = "goldenaudiocassette";
		this.maxslots = 16;
		this.MaxWriteTime = 100;
		setRegistryName(name);
	}
}
