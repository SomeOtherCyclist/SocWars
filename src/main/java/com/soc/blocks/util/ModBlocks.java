package com.soc.blocks.util;

import com.soc.SocWars;
import com.soc.blocks.BigTntBlock;
import com.soc.blocks.CollectibleBlock;
import com.soc.blocks.ColourStateBlock;
import com.soc.entities.BigTntEntity;
import com.soc.items.util.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {
    public static void initialise() {}

    public static final Block SPAWN_PLACEHOLDER = ModBlocks.register("spawn_placeholder", ColourStateBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true); //May need to be more complex to allow choosing which team it's supposed to be for
    public static final Block CENTRE_PLACEHOLDER = ModBlocks.register("centre_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true);

    public static final Block DIAMOND_GEN_PLACEHOLDER = ModBlocks.register("diamond_gen_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true);
    public static final Block EMERALD_GEN_PLACEHOLDER = ModBlocks.register("emerald_gen_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true);
    public static final Block ISLAND_GEN_PLACEHOLDER = ModBlocks.register("island_gen_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true);

    public static final Block NUCLEAR_BOMB = ModBlocks.register("nuclear_bomb", (settings) -> new BigTntBlock(settings, BigTntEntity.BigTntType.NUCLEAR), AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.GRASS), true);
    public static final Block HYDROGEN_BOMB = ModBlocks.register("hydrogen_bomb", (settings) -> new BigTntBlock(settings, BigTntEntity.BigTntType.HYDROGEN), AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.GRASS), true);
    public static final Block COLLECTIBLE_BLOCK = ModBlocks.register("collectible_block", CollectibleBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON), true);
    //public static final Block RED_BEDWARS_BED = ModBlocks.register("red_bedwars_bed", (settings) -> new BedwarsBed(DyeColor.RED, settings), bedSettings(DyeColor.RED), true);
    //public static final Block YELLOW_BEDWARS_BED = ModBlocks.register("yellow_bedwars_bed", (settings) -> new BedwarsBed(DyeColor.YELLOW, settings), bedSettings(DyeColor.YELLOW), true);
    //public static final Block LIME_BEDWARS_BED = ModBlocks.register("lime_bedwars_bed", (settings) -> new BedwarsBed(DyeColor.LIME, settings), bedSettings(DyeColor.LIME), true);
    //public static final Block BLUE_BEDWARS_BED = ModBlocks.register("blue_bedwars_bed", (settings) -> new BedwarsBed(DyeColor.BLUE, settings), bedSettings(DyeColor.BLUE), true);

    public static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {

        // Create a registry key for the block
        RegistryKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            RegistryKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
            ModItems.addItemToGroups(blockItem.asItem(), ModItems.SOCWARS_ITEM_GROUP_KEY);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SocWars.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SocWars.MOD_ID, name));
    }

    private static AbstractBlock.Settings bedSettings(DyeColor color) {
        return AbstractBlock.Settings.create()
                .mapColor(color.getMapColor())
                .sounds(BlockSoundGroup.WOOD)
                .hardness(0.2f)
                .resistance(1000000f)
                .nonOpaque()
                .pistonBehavior(PistonBehavior.BLOCK);
    }
}
