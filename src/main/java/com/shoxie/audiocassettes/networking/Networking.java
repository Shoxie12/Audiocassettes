package com.shoxie.audiocassettes.networking;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Networking {

    public static SimpleNetworkWrapper INSTANCE;
    private static int ID = 0;

    public Networking() {
    }
    
    public static int nextID() {
        return ID++;
    }

    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages();
    }
    
    public static void registerMessages() {
        INSTANCE.registerMessage(
        		TapeDeckStartWritingPacket.Handler.class,
        		TapeDeckStartWritingPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		TapeDeckSetSongPacket.Handler.class,
        		TapeDeckSetSongPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		BoomBoxNextSongPacket.Handler.class,
        		BoomBoxNextSongPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		BoomBoxPrevSongPacket.Handler.class,
        		BoomBoxPrevSongPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		CBoomBoxPlayPacket.Handler.class,
        		CBoomBoxPlayPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		CBoomBoxStopPacket.Handler.class,
        		CBoomBoxStopPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		CWalkmanPlayPacket.Handler.class,
        		CWalkmanPlayPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		CWalkmanStopPacket.Handler.class,
        		CWalkmanStopPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		SBoomBoxPlayPacket.Handler.class,
        		SBoomBoxPlayPacket.class,
        		nextID(),
        		Side.CLIENT);
        
        INSTANCE.registerMessage(
        		SBoomBoxStopPacket.Handler.class,
        		SBoomBoxStopPacket.class,
        		nextID(),
        		Side.CLIENT);
        
        INSTANCE.registerMessage(
        		SWalkmanPlayPacket.Handler.class,
        		SWalkmanPlayPacket.class,
        		nextID(),
        		Side.CLIENT);
        
        INSTANCE.registerMessage(
        		SWalkmanStopPacket.Handler.class,
        		SWalkmanStopPacket.class,
        		nextID(),
        		Side.CLIENT);
        
        INSTANCE.registerMessage(
        		WalkmanNextSongPacket.Handler.class,
        		WalkmanNextSongPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		WalkmanOnDropPacket.Handler.class,
        		WalkmanOnDropPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		WalkmanPrevSongPacket.Handler.class,
        		WalkmanPrevSongPacket.class,
        		nextID(),
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		TapeDeckStopWritePacket.Handler.class,
        		TapeDeckStopWritePacket.class,
        		nextID(),
        		Side.SERVER);
    }
}