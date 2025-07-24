package com.soc.tools;

import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public class ToolMaterials {
    public static final ToolMaterial DASH_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            150,
            12.0F,
            2.5F,
            40,
            null
    );
    public static final ToolMaterial POTIONWEAPON_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            300,
            8.0F,
            3F,
            30,
            null
    );
    public static final ToolMaterial LIFETHIEF_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            350,
            8.0F,
            3F,
            30,
            null
    );
    public static final ToolMaterial DEVASTATOR_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            5,
            2.0F,
            10F,
            20,
            null
    );
}
