package com.soc.materials;

import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public interface ToolMaterials {
    ToolMaterial DASH = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            150,
            12.0f,
            2.5f,
            40,
            null
    );
    ToolMaterial POTIONWEAPON = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            300,
            8.0f,
            3f,
            30,
            null
    );
    ToolMaterial LIFETHIEF = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            350,
            8.0f,
            3f,
            30,
            null
    );
    ToolMaterial DEVASTATOR = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            20,
            2.0f,
            10f,
            20,
            null
    );
    ToolMaterial BASE = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            69,
            7f,
            0f,
            25,
            null
    );
    ToolMaterial EMERALD = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            1600,
            12f,
            3.5f,
            20,
            null
    );
    ToolMaterial UNOBTANIUM = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2000,
            16f,
            4f,
            30,
            null
    );
    ToolMaterial BEDROCK = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2750,
            24f,
            5f,
            40,
            null
    );
    ToolMaterial FISH = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            4000,
            32f,
            6f,
            50,
            null
    );
}
