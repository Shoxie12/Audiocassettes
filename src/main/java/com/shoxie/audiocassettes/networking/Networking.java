package com.shoxie.audiocassettes.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Networking {

    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
        		new ResourceLocation(com.shoxie.audiocassettes.audiocassettes.MODID, "audiocassettes"), () -> "1.0", s -> true, s -> true);

        //TapeDeckStartWritingPacket
        INSTANCE.messageBuilder(TapeDeckStartWritingPacket.class, nextID())
                .encoder(TapeDeckStartWritingPacket::toBytes)
                .decoder(TapeDeckStartWritingPacket::new)
                .consumerMainThread(TapeDeckStartWritingPacket::handle)
                .add();

        //TapeDeckSetSongPacket
        INSTANCE.messageBuilder(TapeDeckSetSongPacket.class, nextID())
                .encoder(TapeDeckSetSongPacket::toBytes)
                .decoder(TapeDeckSetSongPacket::new)
                .consumerMainThread(TapeDeckSetSongPacket::handle)
                .add();

        //CBoomBoxPlayPacket
        INSTANCE.messageBuilder(CBoomBoxPlayPacket.class, nextID())
                .encoder(CBoomBoxPlayPacket::toBytes)
                .decoder(CBoomBoxPlayPacket::new)
                .consumerMainThread(CBoomBoxPlayPacket::handle)
                .add();

        //SBoomBoxPlayPacket
        INSTANCE.messageBuilder(SBoomBoxPlayPacket.class, nextID())
                .encoder(SBoomBoxPlayPacket::toBytes)
                .decoder(SBoomBoxPlayPacket::new)
                .consumerMainThread(SBoomBoxPlayPacket::handle)
                .add();

        //CBoomBoxStopPacket
        INSTANCE.messageBuilder(CBoomBoxStopPacket.class, nextID())
                .encoder(CBoomBoxStopPacket::toBytes)
                .decoder(CBoomBoxStopPacket::new)
                .consumerMainThread(CBoomBoxStopPacket::handle)
                .add();

        //SBoomBoxStopPacket
        INSTANCE.messageBuilder(SBoomBoxStopPacket.class, nextID())
                .encoder(SBoomBoxStopPacket::toBytes)
                .decoder(SBoomBoxStopPacket::new)
                .consumerMainThread(SBoomBoxStopPacket::handle)
                .add();

        //BoomBoxNextSongPacket
        INSTANCE.messageBuilder(BoomBoxNextSongPacket.class, nextID())
                .encoder(BoomBoxNextSongPacket::toBytes)
                .decoder(BoomBoxNextSongPacket::new)
                .consumerMainThread(BoomBoxNextSongPacket::handle)
                .add();

        //BoomBoxPrevSongPacket
        INSTANCE.messageBuilder(BoomBoxPrevSongPacket.class, nextID())
                .encoder(BoomBoxPrevSongPacket::toBytes)
                .decoder(BoomBoxPrevSongPacket::new)
                .consumerMainThread(BoomBoxPrevSongPacket::handle)
                .add();

        //CWalkmanPlayPacket
        INSTANCE.messageBuilder(CWalkmanPlayPacket.class, nextID())
                .encoder(CWalkmanPlayPacket::toBytes)
                .decoder(CWalkmanPlayPacket::new)
                .consumerMainThread(CWalkmanPlayPacket::handle)
                .add();

        //SWalkmanPlayPacket
        INSTANCE.messageBuilder(SWalkmanPlayPacket.class, nextID())
                .encoder(SWalkmanPlayPacket::toBytes)
                .decoder(SWalkmanPlayPacket::new)
                .consumerMainThread(SWalkmanPlayPacket::handle)
                .add();

        //CWalkmanStopPacket
        INSTANCE.messageBuilder(CWalkmanStopPacket.class, nextID())
                .encoder(CWalkmanStopPacket::toBytes)
                .decoder(CWalkmanStopPacket::new)
                .consumerMainThread(CWalkmanStopPacket::handle)
                .add();

        //SWalkmanStopPacket
        INSTANCE.messageBuilder(SWalkmanStopPacket.class, nextID())
                .encoder(SWalkmanStopPacket::toBytes)
                .decoder(SWalkmanStopPacket::new)
                .consumerMainThread(SWalkmanStopPacket::handle)
                .add();

        //WalkmanNextSongPacket
        INSTANCE.messageBuilder(WalkmanNextSongPacket.class, nextID())
                .encoder(WalkmanNextSongPacket::toBytes)
                .decoder(WalkmanNextSongPacket::new)
                .consumerMainThread(WalkmanNextSongPacket::handle)
                .add();

        //WalkmanPrevSongPacket
        INSTANCE.messageBuilder(WalkmanPrevSongPacket.class, nextID())
                .encoder(WalkmanPrevSongPacket::toBytes)
                .decoder(WalkmanPrevSongPacket::new)
                .consumerMainThread(WalkmanPrevSongPacket::handle)
                .add();

        //WalkmanOnDropPacket
        INSTANCE.messageBuilder(WalkmanOnDropPacket.class, nextID())
                .encoder(WalkmanOnDropPacket::toBytes)
                .decoder(WalkmanOnDropPacket::new)
                .consumerMainThread(WalkmanOnDropPacket::handle)
                .add();

        //TapeDeckStopWritePacket
        INSTANCE.messageBuilder(TapeDeckStopWritePacket.class, nextID())
                .encoder(TapeDeckStopWritePacket::toBytes)
                .decoder(TapeDeckStopWritePacket::new)
                .consumerMainThread(TapeDeckStopWritePacket::handle)
                .add();
        
    }
}