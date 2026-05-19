package com.soc.util;

import com.soc.SocWars;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface BlockTags {
    TagKey<Block> IMMUNE = from_string("immune");
    TagKey<Block> EXPLOSION_IMMUNE = from_string("explosion_immune");
    TagKey<Block> NO_BREAK_FROM_WATER = from_string("no_break_from_water");
    TagKey<Block> MAP_PLACEHOLDER = from_string("map_placeholder");
    TagKey<Block> MAP_PLACEHOLDER_WITHOUT_BEDS = from_string("map_placeholder_without_beds");
    TagKey<Block> NO_BLOCK_DROP = from_string("no_block_drop");
    TagKey<Block> DISALLOW_MORPH = from_string("disallow_morph");

    private static TagKey<Block> from_string(String id) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(SocWars.MOD_ID, id));
    }

    static void initialise() {}
}
