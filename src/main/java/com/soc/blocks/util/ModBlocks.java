package com.soc.blocks.util;

import com.soc.SocWars;
import com.soc.blocks.*;
import com.soc.entities.BigTntEntity;
import com.soc.items.BlockItems;
import com.soc.items.FeatherBlockItem;
import com.soc.items.util.ItemGroups;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface ModBlocks {
    static void initialise() {}

    Block WHITER_CONCRETE = ModBlocks.register("whiter_concrete", Block::new, AbstractBlock.Settings.create().mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.BASS).sounds(BlockSoundGroup.STONE).requiresTool().strength(1.8f), true, ItemGroups.BLOCKS_KEY);
    Block JONGLE_LOG = ModBlocks.register("jongle_log", PillarBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(NoteBlockInstrument.BASEDRUM).sounds(BlockSoundGroup.WOOD).requiresTool().strength(2f, 3f).burnable(), true, ItemGroups.BLOCKS_KEY);
    Block JONGLE_PLANKS = ModBlocks.register("jongle_planks", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.DIRT_BROWN).instrument(NoteBlockInstrument.BASEDRUM).sounds(BlockSoundGroup.WOOD).requiresTool().strength(2f, 3f).burnable(), true, ItemGroups.BLOCKS_KEY);
    Block PLASTIC_BLOCK = ModBlocks.register("plastic_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(2f, 2f), true, ItemGroups.BLOCKS_KEY);
    Block RUBBER_BLOCK = ModBlocks.register("rubber_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(4f, 3f), true, ItemGroups.BLOCKS_KEY);
    Block PERSPEX_BLOCK = ModBlocks.register("perspex_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(3f, 1200f), true, ItemGroups.BLOCKS_KEY);
    Block HARDENED_LAVA_BLOCK = ModBlocks.register("hardened_lava_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(50f, 1200f), settings -> settings.rarity(Rarity.RARE), ItemGroups.BLOCKS_KEY);
    Block UNOBTANIUM_BLOCK = ModBlocks.register("unobtanium_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(120f, 1200f), settings -> settings.rarity(Rarity.RARE), ItemGroups.BLOCKS_KEY);
    Block NEAR_INFINITE_DENSITY_BLOCK = ModBlocks.register("near_infinite_density_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(250f, 1200f), settings -> settings.rarity(Rarity.EPIC), ItemGroups.BLOCKS_KEY);
    Block LIAM_BLOCK = ModBlocks.register("liam_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(500f, 1200f), settings -> settings.rarity(Rarity.EPIC), ItemGroups.BLOCKS_KEY);
    ManyBedsBlock MANY_BEDS_BLOCK = ModBlocks.register("many_beds_block", ManyBedsBlock::new, AbstractBlock.Settings.create().requiresTool().strength(2f, 1200f).sounds(BlockSoundGroup.WOOD).nonOpaque(), false, ItemGroups.BLOCKS_KEY);
    Block FEATHER_BLOCK = ModBlocks.register("feather_block", Block::new, AbstractBlock.Settings.create().breakInstantly(), FeatherBlockItem::new, ItemGroups.BLOCKS_KEY);
    Block GALLIUM_BLOCK = ModBlocks.register("gallium_block", GalliumBlock::new, AbstractBlock.Settings.create().requiresTool().strength(0.35f, 2.5f).noCollision(), settings -> settings.rarity(Rarity.UNCOMMON), ItemGroups.BLOCKS_KEY);

    Block SPAWN_PLACEHOLDER = ModBlocks.register("spawn_placeholder", ModifiableColourStateBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true, ItemGroups.MAP_MAKING_TOOLS_KEY);
    Block CENTRE_PLACEHOLDER = ModBlocks.register("centre_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true, ItemGroups.MAP_MAKING_TOOLS_KEY);

    TierBlock CHEST_PLACEHOLDER = ModBlocks.register("chest_placeholder", settings -> new TierBlock(settings, 1) {
        @Override
        protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
            return new ItemStack(BlockItems.CHEST_PLACEHOLDERS.get(state.get(TierBlock.TIER)));
        }
    }, AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).noCollision().nonOpaque(), false, ItemGroups.MAP_MAKING_TOOLS_KEY);

    Block DIAMOND_GEN_PLACEHOLDER = ModBlocks.register("diamond_gen_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true, ItemGroups.MAP_MAKING_TOOLS_KEY);
    Block EMERALD_GEN_PLACEHOLDER = ModBlocks.register("emerald_gen_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true, ItemGroups.MAP_MAKING_TOOLS_KEY);
    Block ISLAND_GEN_PLACEHOLDER = ModBlocks.register("island_gen_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true, ItemGroups.MAP_MAKING_TOOLS_KEY);
    Block INDIVIDUAL_SHOP_PLACEHOLDER = ModBlocks.register("individual_shop_placeholder", SimpleHorizontalFacingBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true, ItemGroups.MAP_MAKING_TOOLS_KEY);
    Block TEAM_SHOP_PLACEHOLDER = ModBlocks.register("team_shop_placeholder", SimpleHorizontalFacingBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true, ItemGroups.MAP_MAKING_TOOLS_KEY);

    Block PROTECTED_AIR = ModBlocks.register("protected_air", Block::new, AbstractBlock.Settings.create().noCollision(), true, ItemGroups.MAP_MAKING_TOOLS_KEY);

    Block MOSHPIT_MAP = ModBlocks.register("moshpit_map", SurvivalIntangibleBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOL).noCollision().nonOpaque(), true, ItemGroups.BLOCKS_KEY); //Maybe fix voxel shape

    Block NUCLEAR_BOMB = ModBlocks.register("nuclear_bomb", settings -> new BigTntBlock(settings, BigTntEntity.BigTntType.NUCLEAR), AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.GRASS), true, ItemGroups.BLOCKS_KEY);
    Block HYDROGEN_BOMB = ModBlocks.register("hydrogen_bomb", settings -> new BigTntBlock(settings, BigTntEntity.BigTntType.HYDROGEN), AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.GRASS), true, ItemGroups.BLOCKS_KEY);
    Block COLLECTIBLE_BLOCK = ModBlocks.register("collectible_block", CollectibleBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON).nonOpaque().luminance(state -> 2), true, ItemGroups.BLOCKS_KEY); //No collision?
    Block DISPLAY_BLOCK = ModBlocks.register("display_block", DisplayBlock::new, AbstractBlock.Settings.create().nonOpaque().noCollision(), true, ItemGroups.BLOCKS_KEY);
    Block KIT_BLOCK = ModBlocks.register("kit_block", KitBlock::new, AbstractBlock.Settings.create(), true, ItemGroups.BLOCKS_KEY);
    Block ITSEVOCAT_SKULL = ModBlocks.register("itsevocat_skull", ItsevocatSkull::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.BAMBOO).nonOpaque(), true, ItemGroups.BLOCKS_KEY); //No collision?
    Block MAP_BLOCK = ModBlocks.register("map_block", MapBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON), true, ItemGroups.BLOCKS_KEY);
    Block JOIN_QUEUE_BLOCK = ModBlocks.register("join_queue_block", JoinQueueBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).hardness(1000000f).resistance(1000000f).nonOpaque().pistonBehavior(PistonBehavior.BLOCK), true, ItemGroups.BLOCKS_KEY);

    Block PASSABLE_INVISIBLE_BLOCK = ModBlocks.register("passable_invisible_block", SurvivalIntangibleBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).hardness(1000000f).resistance(1000000f).nonOpaque().noCollision().pistonBehavior(PistonBehavior.BLOCK), true, ItemGroups.BLOCKS_KEY);

    /*
    public static final Block SOC_HEAD = ModBlocks.register(
            "soc_head",
            settings -> new PlayerSkullBlock(settings),
            AbstractBlock.Settings.create().instrument(NoteBlockInstrument.ZOMBIE).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY),
            true
    );
    public static final Block ITSEVOCAT_HEAD = ModBlocks.register(
            "itsevocat_head",
            settings -> new SkullBlock(SkullBlock.Type.ZOMBIE, settings),
            AbstractBlock.Settings.create().instrument(NoteBlockInstrument.ZOMBIE).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY),
            true
    );
     */

    static <T extends Block> T register(String name, Function<AbstractBlock.Settings, T> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem, RegistryKey<ItemGroup> blocksKey) {
        return ModBlocks.register(name, blockFactory, settings, shouldRegisterItem ? UnaryOperator.identity() : null, blocksKey);
    }

    static <T extends Block> T register(String name, Function<AbstractBlock.Settings, T> blockFactory, AbstractBlock.Settings settings, @Nullable UnaryOperator<Item.Settings> itemSettings, RegistryKey<ItemGroup> blocksKey) {
        return ModBlocks.register(name, blockFactory, settings, itemSettings == null ? null : (block, itemKey) -> new BlockItem(block, itemSettings.apply(new Item.Settings().registryKey(itemKey))), blocksKey);
    }

    static <T extends Block> T register(String name, Function<AbstractBlock.Settings, T> blockFactory, AbstractBlock.Settings settings, @Nullable BiFunction<Block, RegistryKey<Item>, ? extends BlockItem> itemFunction, RegistryKey<ItemGroup> blocksKey) {
        // Create options registry key for the block
        RegistryKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        T block = blockFactory.apply(settings.registryKey(blockKey));

        if (itemFunction != null) {
            // Items need to be registered with options different type of registry key, but the ID
            // can be the same.
            RegistryKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = itemFunction.apply(block, itemKey);
            Registry.register(Registries.ITEM, itemKey, blockItem);
            ItemGroups.addItemToGroupsAndBaseItemGroup(blockItem.asItem(), blocksKey);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SocWars.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SocWars.MOD_ID, name));
    }
}
