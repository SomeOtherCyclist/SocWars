package com.soc.util;

import com.soc.SocWars;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface BlockTags {
    TagKey<Block> EXPLOSION_IMMUNE = from_string("explosion_immune");
    TagKey<Block> MAP_PLACEHOLDER = from_string("map_placeholder");

    private static TagKey<Block> from_string(String id) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(SocWars.MOD_ID, id));
    }
}
