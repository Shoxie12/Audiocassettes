package com.shoxie.audiocassettes;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.proxy.CommonProxy;

@Mod(modid = audiocassettes.MODID, name = audiocassettes.NAME, version = audiocassettes.VERSION)
public class audiocassettes
{
    public static final String MODID = "audiocassettes";
    public static final String NAME = "AudioCassettes";
    public static final String VERSION = "1.1";
    public static Logger logger = LogManager.getLogger(MODID);
    public static Configuration config;
    
    public static final String EMPTY_SOUND = "empty";
    
    @Mod.Instance(MODID)
	public static audiocassettes instance;
	
	@SidedProxy(clientSide = "com.shoxie.audiocassettes.proxy.ClientProxy", serverSide = "com.shoxie.audiocassettes.proxy.CommonProxy")
	public static CommonProxy proxy;
	public static int BoomBoxMaxSoundDistance;
	public static int WalkmanMaxSoundDistance;
	public static boolean announceenabled;
	public static boolean skipemptyslots;
    
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {	
        logger = e.getModLog();
        proxy.preInit(e);
		File directory = e.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "audiocassettes.cfg"));
		ModConfig.readConfig();

        Networking.registerMessages("audiocassettes");
        audiocassettes.BoomBoxMaxSoundDistance = ModConfig.boomboxbmaxdist;
        audiocassettes.WalkmanMaxSoundDistance = ModConfig.walkmanmaxdist;
        audiocassettes.announceenabled = ModConfig.nowplayingannounce;
        audiocassettes.skipemptyslots = ModConfig.skipemptyslots;

    }

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
        if (config.hasChanged()) {
            config.save();
        }
	}
}
