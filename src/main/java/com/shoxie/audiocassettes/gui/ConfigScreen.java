package com.shoxie.audiocassettes.gui;

import com.shoxie.audiocassettes.ModOptions;
import com.shoxie.audiocassettes.audiocassettes;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.network.chat.Component;

public final class ConfigScreen extends SimpleOptionsSubScreen {

    private static final OptionInstance<?>[] OPTIONS = {
            ModOptions.NOW_PLAYING_ANNOUNCE,
            ModOptions.SKIP_EMPTY_SLOTS,
//            ModOptions.BOOMBOX_DISTANCE,
//            ModOptions.WALKMAN_DISTANCE
    };

    public ConfigScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, Component.translatable("gui.audiocassettes.configscreen.title",
                audiocassettes.NAME),OPTIONS);
    }
}