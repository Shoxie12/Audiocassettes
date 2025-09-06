package com.shoxie.audiocassettes;

import java.nio.file.Path;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent.Loading;
import net.minecraftforge.fml.event.config.ModConfigEvent.Reloading;

@Mod.EventBusSubscriber(modid = audiocassettes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	//private static ForgeConfigSpec.ConfigValue<Integer> walkmanmaxdist;
	//private static ForgeConfigSpec.ConfigValue<Integer> boomboxbmaxdist;
	private static ForgeConfigSpec.ConfigValue<Boolean> nowplayingannounce;
	private static ForgeConfigSpec.ConfigValue<Boolean> skipemptyslots;
    public static ForgeConfigSpec cfg;


    static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("AudioCassettes");
		
		nowplayingannounce = builder.comment(
				"",
				"Announce the name of the song currently playing to everyone").define("NowPlayingAnnounce", true);
		
		skipemptyslots = builder.comment(
				"",
				"If enabled, Walkman and Boombox will ignore empty slots of audio cassettes").define("SkipEmptySlots", true);

//		boomboxbmaxdist = builder.comment(
//				"",
//				"Sets max distance (blocks) for a BoomBox where you can hear music").define("BoomBoxMaxDistance", 64);
//
//		walkmanmaxdist = builder.comment(
//				"",
//				"Sets max distance (blocks) for a Walkman where you can hear music").define("WalkmanMaxDistance", 28);
		
		builder.pop();
		cfg = builder.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onReload(final Reloading configEvent) {
    }
    
    @SubscribeEvent
    public static void onLoad(final Loading configEvent) {

    }
	
	public static boolean isNowPlayingAnnounce() {
		return nowplayingannounce.get();
	}

    public static void setNowPlayingAnnounce(boolean val) {
        nowplayingannounce.set(val);
    }
	
//	public static int getBoomBoxMaxDist() {
//		return boomboxbmaxdist.get();
//	}
//
//    public static void setBoomBoxMaxDist(int val) {
//        boomboxbmaxdist.set(val);
//    }
//
//	public static int getWalkmanMaxDist() {
//		return walkmanmaxdist.get();
//	}
//
//    public static void setWalkmanMaxDist(int val) {
//        walkmanmaxdist.set(val);
//    }
	
	public static boolean SkipEmptySlots() {
		return skipemptyslots.get();
	}

    public static void setSkipEmptySlots(boolean val) {
        skipemptyslots.set(val);
    }
    
}