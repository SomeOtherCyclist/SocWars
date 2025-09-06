package com.soc.blocks.util;

import com.soc.SocWars;
import com.soc.blocks.BigTnt;
import com.soc.blocks.CollectibleBlock;
import com.soc.blocks.DepositableChestBlock;
import com.soc.entities.BigTntEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {
    public static void initialise() {
        DepositableChestBlock.initialise();
    }

    public static final Block NUCLEAR_BOMB = ModBlocks.register("nuclear_bomb", (settings) -> new BigTnt(settings, BigTntEntity.BigTntType.NUCLEAR), AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.GRASS), true);
    public static final Block HYDROGEN_BOMB = ModBlocks.register("hydrogen_bomb", (settings) -> new BigTnt(settings, BigTntEntity.BigTntType.HYDROGEN), AbstractBlock.Settings.create().breakInstantly(), true);
    public static final Block COLLECTIBLE_BLOCK = ModBlocks.register("collectible_block", CollectibleBlock::new, AbstractBlock.Settings.create(), true);

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
