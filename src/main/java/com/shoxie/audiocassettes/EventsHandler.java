package com.shoxie.audiocassettes;

import com.shoxie.audiocassettes.capability.WalkmanCapability;
import com.shoxie.audiocassettes.gui.BoomBoxScreen;
import com.shoxie.audiocassettes.gui.TapeDeckScreen;
import com.shoxie.audiocassettes.gui.WalkmanScreen;
import com.shoxie.audiocassettes.init.Init;
import com.shoxie.audiocassettes.item.WalkmanItem;
import com.shoxie.audiocassettes.networking.Networking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.shoxie.audiocassettes.audiocassettes.proxy;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventsHandler {

    //Screens
    @SubscribeEvent
    public static void ScreenInit(FMLClientSetupEvent event) {
        if(audiocassettes.isModLoaded()) {
            MenuScreens.register(Init.BOOMBOX_MENU.get(), BoomBoxScreen::new);
            MenuScreens.register(Init.TAPEDECK_MENU.get(), TapeDeckScreen::new);
            MenuScreens.register(Init.WALKMAN_MENU.get(), WalkmanScreen::new);
            proxy.ScreenInit();
        }
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        if(audiocassettes.isModLoaded())
            event.enqueueWork(Networking::registerMessages);
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if(audiocassettes.isModLoaded()) {
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(Init.BOOMBOX_ITEM);
                event.accept(Init.TAPEDECK_ITEM);
                event.accept(Init.DIAMOND_AUDIO_CASSETTE);
                event.accept(Init.GOLDEN_AUDIO_CASSETTE);
                event.accept(Init.IRON_AUDIO_CASSETTE);
                event.accept(Init.WALKMAN);
                event.accept(Init.CASSETTE_FRAME);
                event.accept(Init.MAGNETIC_TAPE);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = audiocassettes.MODID)
    public static class CapEventsHandler {
        @SubscribeEvent
        public static void handle(final AttachCapabilitiesEvent<ItemStack> event) {
            if(audiocassettes.isModLoaded()) {
                var it = event.getObject().getItem();
                if (it instanceof WalkmanItem)
                    event.addCapability(audiocassettes.cap_loc, new WalkmanCapability());
            }
        }
    }
}