package com.shoxie.audiocassettes;

import net.minecraftforge.common.config.Configuration;

public class ModConfig {
	
	public static boolean nowplayingannounce = true;
	public static boolean skipemptyslots = true;
	public static int boomboxbmaxdist = 64;
	public static int walkmanmaxdist = 28;

    public static void readConfig() {
        Configuration cfg = audiocassettes.config;
        try {
            cfg.load();
            loadConfig(cfg);
        } catch (Exception e) {
        	audiocassettes.logger.error("Audio Cassettes Can't load config file :(", e);
        }
    }

    private static void loadConfig(Configuration cfg) {
    	nowplayingannounce = cfg.getBoolean("NowPlayingAnnounce", "general", nowplayingannounce, "Announce the name of the song currently playing to everyone?");
    	skipemptyslots = cfg.getBoolean("SkipEmptySlots", "general", skipemptyslots, "If enabled, Walkman and Boombox will ignore empty slots of audio cassettes");
    	boomboxbmaxdist = cfg.getInt("BoomBoxMaxDistance", "general", boomboxbmaxdist, 16, 512, "Max distance for a BoomBox where you can hear music (works only with stereo music)");
    	walkmanmaxdist = cfg.getInt("WalkmanMaxDistance", "general", walkmanmaxdist, 16, 512, "Max distance for a Walkman where you can hear music (works only with stereo music)");
    }
}