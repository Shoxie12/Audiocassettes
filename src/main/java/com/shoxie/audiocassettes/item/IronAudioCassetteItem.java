package com.shoxie.audiocassettes.item;

public class IronAudioCassetteItem extends AbstractAudioCassetteItem{

	public IronAudioCassetteItem() {
		super();
		this.name = "ironaudiocassette";
		this.maxslots = 10;
		this.MaxWriteTime = 150;
		setRegistryName(name);
	}
}
