package com.shoxie.audiocassettes;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shoxie.audiocassettes.container.BoomBoxContainer;
import com.shoxie.audiocassettes.container.WalkmanContainer;
import com.shoxie.audiocassettes.container.TapeDeckContainer;
import com.shoxie.audiocassettes.networking.Networking;
import com.shoxie.audiocassettes.proxy.ClientProxy;
import com.shoxie.audiocassettes.proxy.IProxy;
import com.shoxie.audiocassettes.proxy.ServerProxy;
import com.shoxie.audiocassettes.tile.BoomBoxTile;
import com.shoxie.audiocassettes.tile.TapeDeckTile;

@Mod("audiocassettes")
public class audiocassettes
{
    public static final String MODID = "audiocassettes";
    public static final String NAME = "Audio Cassettes";
    public static final String VERSION = "1.0";
    public static Logger logger = LogManager.getLogger(MODID);
    public static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
    public static final String EMPTY_SOUND = "empty";
    public static int WalkmanMaxSoundDistance = 28;
    public static int BoomBoxMaxSoundDistance = 64;
    public static boolean announceenabled = true;
	
    public audiocassettes() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistryEvents::ScreenInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistryEvents::NetworkingInit);
        ModConfig.loadConfig(ModConfig.cfg, FMLPaths.CONFIGDIR.get().resolve("audiocassettes-common.toml"));
        BoomBoxMaxSoundDistance = ModConfig.getBoomBoxMaxDist();
        WalkmanMaxSoundDistance = ModConfig.getWalkmanMaxDist();
        announceenabled = ModConfig.isNowPlayingAnnounce();
    }
    
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	
    	public static void ScreenInit(final FMLClientSetupEvent event) {
    		proxy.ScreenInit();
    	}
    	
        public static void NetworkingInit(final FMLCommonSetupEvent event) {
            Networking.registerMessages();
        }
        
    	@SubscribeEvent
    	public static void onSoundRegister(final RegistryEvent.Register<SoundEvent> e) {
    		e.getRegistry().register(ModSoundEvents.EMPTY);
    	}
        
        @SubscribeEvent
        public static void onBlockRegister(final RegistryEvent.Register<Block> e) {
        	e.getRegistry().register(ModBlocks.TAPE_DECK);
        	e.getRegistry().register(ModBlocks.BOOMBOX);
        }
        
        @SubscribeEvent
        public static void onItemRegister(final RegistryEvent.Register<Item> e) {
        	e.getRegistry().register(ModItems.IRON_AUDIO_CASSETTE);
        	e.getRegistry().register(ModItems.DIAMOND_AUDIO_CASSETTE);
        	e.getRegistry().register(ModItems.GOLDEN_AUDIO_CASSETTE);
        	e.getRegistry().register(ModItems.WALKMAN);
        	e.getRegistry().register(ModItems.MAGNETIC_TAPE);
        	e.getRegistry().register(ModItems.CASSETTE_FRAME);
        	e.getRegistry().register(new BlockItem(ModBlocks.TAPE_DECK, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(ModBlocks.TAPE_DECK.getRegistryName()));
        	e.getRegistry().register(new BlockItem(ModBlocks.BOOMBOX, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(ModBlocks.BOOMBOX.getRegistryName()));
        }
        
        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
        	event.getRegistry().register(TileEntityType.Builder.create(TapeDeckTile::new, ModBlocks.TAPE_DECK).build(null).setRegistryName("tapedeck"));
        	event.getRegistry().register(TileEntityType.Builder.create(BoomBoxTile::new, ModBlocks.BOOMBOX).build(null).setRegistryName("boombox"));
        }
        
        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                TapeDeckTile tile = (TapeDeckTile) proxy.getClientWorld().getTileEntity(pos);
                return new TapeDeckContainer(windowId, tile, inv);
            }).setRegistryName("tapedeck"));
            
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                BoomBoxTile tile = (BoomBoxTile) proxy.getClientWorld().getTileEntity(pos);
                return new BoomBoxContainer(windowId, tile, inv);
            }).setRegistryName("boombox"));
            
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                return new WalkmanContainer(windowId,inv,proxy.getClientPlayer());
            }).setRegistryName(new ResourceLocation(MODID, "walkman")));
        }
    }
}
