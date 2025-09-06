package com.shoxie.audiocassettes;

import com.shoxie.audiocassettes.init.Init;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shoxie.audiocassettes.proxy.ClientProxy;
import com.shoxie.audiocassettes.proxy.IProxy;
import com.shoxie.audiocassettes.proxy.ServerProxy;

@Mod("audiocassettes")
public class audiocassettes
{

    public static final String MODID = "audiocassettes";
    public static final String NAME = "AudioCassettes";
    public static final String VERSION = "1.2";
    public static final ResourceLocation cap_loc = new ResourceLocation(MODID, "walkmancapability");
    public static Logger logger = LogManager.getLogger(MODID);
    public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static final String EMPTY_SOUND = "empty";
    public static int WalkmanMaxSoundDistance = 16;
    public static int BoomBoxMaxSoundDistance = 16;
    public static boolean announceenabled = true;
    public static boolean skipemptyslots = true;
	
    public audiocassettes() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.cfg);
        Config.loadConfig(Config.cfg, FMLPaths.CONFIGDIR.get().resolve("audiocassettes-common.toml"));
//        BoomBoxMaxSoundDistance = Config.getBoomBoxMaxDist();
//        WalkmanMaxSoundDistance = Config.getWalkmanMaxDist();
        announceenabled = Config.isNowPlayingAnnounce();
        skipemptyslots = Config.SkipEmptySlots();
        final var bus = FMLJavaModLoadingContext.get().getModEventBus();
        Init.BLOCKS.register(bus);
        Init.SOUND_EVENTS.register(bus);
        Init.ITEMS.register(bus);
        Init.BLOCK_ENTITIES.register(bus);
        Init.MENU_TYPES.register(bus);
    }

    public static boolean isModLoaded() {
        return Init.BOOMBOX.isPresent() && Init.TAPEDECK.isPresent();
    }
}
