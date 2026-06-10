package com.soc.util;

import com.soc.SocWars;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface ModBlockTags {
    TagKey<Block> IMMUNE = fromString("immune");
    TagKey<Block> EXPLOSION_IMMUNE = fromString("explosion_immune");
    TagKey<Block> NO_BREAK_FROM_WATER = fromString("no_break_from_water");
    TagKey<Block> MAP_PLACEHOLDER = fromString("map_placeholder");
    TagKey<Block> MAP_PLACEHOLDER_WITHOUT_BEDS = fromString("map_placeholder_without_beds");
    TagKey<Block> NO_BLOCK_DROP = fromString("no_block_drop");
    TagKey<Block> DISALLOW_MORPH = fromString("disallow_morph");

    private static TagKey<Block> fromString(String id) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(SocWars.MOD_ID, id));
    }

    static void initialise() {}
}
