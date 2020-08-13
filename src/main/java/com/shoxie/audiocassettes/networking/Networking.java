package com.shoxie.audiocassettes.networking;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {

    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(com.shoxie.audiocassettes.audiocassettes.MODID, "audiocassettes"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(),
        		TapeDeckStartWritingPacket.class,
        		TapeDeckStartWritingPacket::toBytes,
        		TapeDeckStartWritingPacket::new,
        		TapeDeckStartWritingPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		TapeDeckSetSongPacket.class,
        		TapeDeckSetSongPacket::toBytes,
        		TapeDeckSetSongPacket::new,
        		TapeDeckSetSongPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		CBoomBoxPlayPacket.class,
        		CBoomBoxPlayPacket::toBytes,
        		CBoomBoxPlayPacket::new,
        		CBoomBoxPlayPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		SBoomBoxPlayPacket.class,
        		SBoomBoxPlayPacket::toBytes,
        		SBoomBoxPlayPacket::new,
        		SBoomBoxPlayPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		CBoomBoxStopPacket.class,
        		CBoomBoxStopPacket::toBytes,
        		CBoomBoxStopPacket::new,
        		CBoomBoxStopPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		SBoomBoxStopPacket.class,
        		SBoomBoxStopPacket::toBytes,
        		SBoomBoxStopPacket::new,
        		SBoomBoxStopPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		BoomBoxNextSongPacket.class,
        		BoomBoxNextSongPacket::toBytes,
        		BoomBoxNextSongPacket::new,
        		BoomBoxNextSongPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		BoomBoxPrevSongPacket.class,
        		BoomBoxPrevSongPacket::toBytes,
        		BoomBoxPrevSongPacket::new,
        		BoomBoxPrevSongPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		CWalkmanPlayPacket.class,
        		CWalkmanPlayPacket::toBytes,
        		CWalkmanPlayPacket::new,
        		CWalkmanPlayPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		SWalkmanPlayPacket.class,
        		SWalkmanPlayPacket::toBytes,
        		SWalkmanPlayPacket::new,
        		SWalkmanPlayPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		CWalkmanStopPacket.class,
        		CWalkmanStopPacket::toBytes,
        		CWalkmanStopPacket::new,
        		CWalkmanStopPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		SWalkmanStopPacket.class,
        		SWalkmanStopPacket::toBytes,
        		SWalkmanStopPacket::new,
        		SWalkmanStopPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		WalkmanNextSongPacket.class,
        		WalkmanNextSongPacket::toBytes,
        		WalkmanNextSongPacket::new,
        		WalkmanNextSongPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		WalkmanPrevSongPacket.class,
        		WalkmanPrevSongPacket::toBytes,
        		WalkmanPrevSongPacket::new,
        		WalkmanPrevSongPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		WalkmanOnDropPacket.class,
        		WalkmanOnDropPacket::toBytes,
        		WalkmanOnDropPacket::new,
        		WalkmanOnDropPacket::handle);
        
    }
}