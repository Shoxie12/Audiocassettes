package com.shoxie.audiocassettes.handler;

import com.shoxie.audiocassettes.ModBlocks;
import com.shoxie.audiocassettes.ModItems;
import com.shoxie.audiocassettes.ModSoundEvents;
import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.tile.TileBoomBox;
import com.shoxie.audiocassettes.tile.TileTapeDeck;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class RegisterHandler {
	@SubscribeEvent
	public static void onSoundRegister(Register<SoundEvent> e) {
		e.getRegistry().register(ModSoundEvents.EMPTY);
	}
	
	@SubscribeEvent
	public static void onBlockRegister(Register<Block> e) {
		e.getRegistry().register(ModBlocks.BOOMBOX);
		e.getRegistry().register(ModBlocks.TAPE_DECK);
		
		GameRegistry.registerTileEntity(TileBoomBox.class, audiocassettes.MODID + "_boombox");
		GameRegistry.registerTileEntity(TileTapeDeck.class, audiocassettes.MODID + "_tapedeck");
	}
	
	@SubscribeEvent
	public static void onItemRegister(Register<Item> e) {
		e.getRegistry().register(ModItems.CASSETTE_FRAME);
		e.getRegistry().register(ModItems.DIAMOND_AUDIO_CASSETTE);
		e.getRegistry().register(ModItems.GOLDEN_AUDIO_CASSETTE);
		e.getRegistry().register(ModItems.IRON_AUDIO_CASSETTE);
		e.getRegistry().register(ModItems.MAGNETIC_TAPE);
		e.getRegistry().register(ModItems.WALKMAN);
		e.getRegistry().register(new ItemBlock(ModBlocks.BOOMBOX).setRegistryName(ModBlocks.BOOMBOX.getRegistryName()));
		e.getRegistry().register(new ItemBlock(ModBlocks.TAPE_DECK).setRegistryName(ModBlocks.TAPE_DECK.getRegistryName()));
	}
}
