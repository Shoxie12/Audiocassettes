package com.shoxie.audiocassettes.handler;

import com.shoxie.audiocassettes.ModBlocks;
import com.shoxie.audiocassettes.ModItems;
import com.shoxie.audiocassettes.audiocassettes;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RenderHandler {
	@SubscribeEvent
	public static void onModelLoad(ModelRegistryEvent e) {
		audiocassettes.proxy.renderItem(ModItems.CASSETTE_FRAME, 0, ModItems.CASSETTE_FRAME.getRegistryName());
		audiocassettes.proxy.renderItem(ModItems.DIAMOND_AUDIO_CASSETTE, 0, ModItems.DIAMOND_AUDIO_CASSETTE.getRegistryName());
		audiocassettes.proxy.renderItem(ModItems.GOLDEN_AUDIO_CASSETTE, 0, ModItems.GOLDEN_AUDIO_CASSETTE.getRegistryName());
		audiocassettes.proxy.renderItem(ModItems.IRON_AUDIO_CASSETTE, 0, ModItems.IRON_AUDIO_CASSETTE.getRegistryName());
		audiocassettes.proxy.renderItem(ModItems.MAGNETIC_TAPE, 0, ModItems.MAGNETIC_TAPE.getRegistryName());
		audiocassettes.proxy.renderItem(ModItems.WALKMAN, 0, ModItems.WALKMAN.getRegistryName());
		audiocassettes.proxy.renderItem(Item.getItemFromBlock(ModBlocks.BOOMBOX), 0, ModBlocks.BOOMBOX.getRegistryName());
		audiocassettes.proxy.renderItem(Item.getItemFromBlock(ModBlocks.TAPE_DECK), 0, ModBlocks.TAPE_DECK.getRegistryName());
		
	}
}