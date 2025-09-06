package com.shoxie.audiocassettes.init;

import com.shoxie.audiocassettes.audiocassettes;
import com.shoxie.audiocassettes.block.BoomBoxBlock;
import com.shoxie.audiocassettes.block.TapeDeckBlock;
import com.shoxie.audiocassettes.entity.BoomBoxEntity;
import com.shoxie.audiocassettes.entity.TapeDeckEntity;
import com.shoxie.audiocassettes.item.*;
import com.shoxie.audiocassettes.menu.BoomBoxMenu;
import com.shoxie.audiocassettes.menu.TapeDeckMenu;
import com.shoxie.audiocassettes.menu.WalkmanMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Init {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, audiocassettes.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, audiocassettes.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, audiocassettes.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, audiocassettes.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, audiocassettes.MODID);

    //Blocks, BIs, BEs
    public static final RegistryObject<BoomBoxBlock> BOOMBOX = BLOCKS.register("boombox", () -> new BoomBoxBlock(BlockBehaviour.Properties.of().strength(3.0f, 10f)));
    public static final RegistryObject<TapeDeckBlock> TAPEDECK = BLOCKS.register("tapedeck", () -> new TapeDeckBlock(BlockBehaviour.Properties.of().strength(3.0f, 10f)));

    public static final RegistryObject<Item> BOOMBOX_ITEM =
            ITEMS.register("boombox", () -> new BlockItem(BOOMBOX.get(), new Item.Properties()));

    public static final RegistryObject<Item> TAPEDECK_ITEM =
            ITEMS.register("tapedeck", () -> new BlockItem(TAPEDECK.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<BoomBoxEntity>> BOOM_BOX_ENTITY =
            BLOCK_ENTITIES.register("boombox",
                    () -> BlockEntityType.Builder.of(BoomBoxEntity::new, BOOMBOX.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<TapeDeckEntity>> TAPEDECK_ENTITY =
            BLOCK_ENTITIES.register("tapedeck",
                    () -> BlockEntityType.Builder.of(TapeDeckEntity::new, TAPEDECK.get())
                            .build(null));
    //Items
    public static final RegistryObject<IronAudioCassetteItem> IRON_AUDIO_CASSETTE =
            ITEMS.register(IronAudioCassetteItem.name, IronAudioCassetteItem::new);
    public static final RegistryObject<GoldenAudioCassetteItem> GOLDEN_AUDIO_CASSETTE =
            ITEMS.register(GoldenAudioCassetteItem.name, GoldenAudioCassetteItem::new);
    public static final RegistryObject<DiamondAudioCassetteItem> DIAMOND_AUDIO_CASSETTE =
            ITEMS.register(DiamondAudioCassetteItem.name, DiamondAudioCassetteItem::new);
    public static final RegistryObject<WalkmanItem> WALKMAN =
            ITEMS.register(WalkmanItem.name, WalkmanItem::new);
    public static final RegistryObject<CassetteFrameItem> CASSETTE_FRAME =
            ITEMS.register(CassetteFrameItem.name, CassetteFrameItem::new);
    public static final RegistryObject<MagneticTapeItem> MAGNETIC_TAPE =
            ITEMS.register(MagneticTapeItem.name, MagneticTapeItem::new);

    //SoundEvents
    public static final RegistryObject<SoundEvent> BLANK_RECORD_SOUND_EVENT =
            SOUND_EVENTS.register(audiocassettes.EMPTY_SOUND, () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(audiocassettes.MODID, audiocassettes.EMPTY_SOUND),128.0F));


    //Menus
    public static final RegistryObject<MenuType<BoomBoxMenu>> BOOMBOX_MENU = MENU_TYPES.register("boombox",
            () -> IForgeMenuType.create(BoomBoxMenu::new));

    public static final RegistryObject<MenuType<WalkmanMenu>> WALKMAN_MENU = MENU_TYPES.register("walkman",
            () -> IForgeMenuType.create(WalkmanMenu::new));

    public static final RegistryObject<MenuType<TapeDeckMenu>> TAPEDECK_MENU = MENU_TYPES.register("tapedeck",
            () -> IForgeMenuType.create(TapeDeckMenu::new));

}
