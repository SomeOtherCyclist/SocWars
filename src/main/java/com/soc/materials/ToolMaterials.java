package com.soc.materials;

import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public interface ToolMaterials {
    ToolMaterial DASH = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            150,
            12.0F,
            2.5F,
            40,
            null
    );
    ToolMaterial POTIONWEAPON = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            300,
            8.0F,
            3F,
            30,
            null
    );
    ToolMaterial LIFETHIEF = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            350,
            8.0F,
            3F,
            30,
            null
    );
    ToolMaterial DEVASTATOR = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            5,
            2.0F,
            10F,
            20,
            null
    );
    ToolMaterial BASE = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            69,
            7F,
            0F,
            25,
            null
    );
}
