package com.shoxie.audiocassettes;

import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class ModOptions {
    public static final OptionInstance<Boolean> NOW_PLAYING_ANNOUNCE =
            OptionInstance.createBoolean(
                    "options.audiocassettes.nowplayingannounce", OptionInstance.cachedConstantTooltip(Component.translatable("options.audiocassettes.nowplayingannounce.tooltip")),
                    Config.isNowPlayingAnnounce(), (a) -> Config.setNowPlayingAnnounce(!Config.isNowPlayingAnnounce()));

    public static final OptionInstance<Boolean> SKIP_EMPTY_SLOTS =
            OptionInstance.createBoolean(
                    "options.audiocassettes.skipemptyslots", OptionInstance.cachedConstantTooltip(Component.translatable("options.audiocassettes.skipemptyslots.tooltip")),
                    Config.SkipEmptySlots(), (a) -> Config.setSkipEmptySlots(!Config.SkipEmptySlots())
            );

//    public static final OptionInstance<Integer> BOOMBOX_DISTANCE = new OptionInstance<>("options.audiocassettes.boomboxbmaxdist",
//            OptionInstance.cachedConstantTooltip(Component.translatable("options.audiocassettes.boomboxbmaxdist.tooltip")),
//            (p_232048_, p_232049_) -> Component.literal(Component.translatable("options.audiocassettes.boomboxbmaxdist").getString()+": "+ p_232049_),
//            (new OptionInstance.IntRange(2, 16)).xmap((p_232003_) -> p_232003_, (p_232094_) -> p_232094_),
//            Codec.intRange(2, 16), Config.getBoomBoxMaxDist(), Config::setBoomBoxMaxDist);
//
//    public static final OptionInstance<Integer> WALKMAN_DISTANCE = new OptionInstance<>("options.audiocassettes.walkmanmaxdist",
//            OptionInstance.cachedConstantTooltip(Component.translatable("options.audiocassettes.walkmanmaxdist.tooltip")),
//            (p_232048_, p_232049_) -> Component.literal(Component.translatable("options.audiocassettes.walkmanmaxdist").getString()+": "+ p_232049_),
//            (new OptionInstance.IntRange(2, 16)).xmap((p_232003_) -> p_232003_, (p_232094_) -> p_232094_),
//            Codec.intRange(2, 16), Config.getWalkmanMaxDist(), Config::setWalkmanMaxDist);
}
