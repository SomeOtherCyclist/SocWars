package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.materials.ToolMaterials;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.component.type.WeaponComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Rarity;

import java.util.List;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class BaseTool extends Item {

    public BaseTool(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(STONE_SHEARS, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(DIAMOND_SHEARS, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(EMERALD_PICKAXE, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(EMERALD_AXE, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(UNOBTANIUM_PICKAXE, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(UNOBTANIUM_AXE, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(UNOBTANIUM_SHEARS, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(BEDROCK_PICKAXE, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(BEDROCK_AXE, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(THE_PICKAXE, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(THE_AXE, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(THE_SHEARS, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(FISHAXE, ItemGroups.TOOLS);
    }

    public static final Item STONE_SHEARS = ModItems.register("stone_shears", BaseTool::new, new Settings().component(DataComponentTypes.TOOL, createShearsComponent(ToolMaterial.STONE)).enchantable(5).maxDamage(ToolMaterial.STONE.durability()));
    public static final Item DIAMOND_SHEARS = ModItems.register("diamond_shears", BaseTool::new, new Settings().component(DataComponentTypes.TOOL, createShearsComponent(ToolMaterial.DIAMOND)).enchantable(10).maxDamage(ToolMaterial.DIAMOND.durability()));
    public static final Item EMERALD_PICKAXE = ModItems.register("emerald_pickaxe", BaseTool::new, new Settings().pickaxe(ToolMaterials.EMERALD, 1f, -2.8f));
    public static final Item EMERALD_AXE = ModItems.register("emerald_axe", BaseTool::new, new Settings().axe(ToolMaterials.EMERALD, 5f, -3f));
    public static final Item UNOBTANIUM_PICKAXE = ModItems.register("unobtanium_pickaxe", BaseTool::new, new Settings().pickaxe(ToolMaterials.UNOBTANIUM, 1f, -2.8f));
    public static final Item UNOBTANIUM_AXE = ModItems.register("unobtanium_axe", BaseTool::new, new Settings().axe(ToolMaterials.UNOBTANIUM, 5f, -3f));
    public static final Item UNOBTANIUM_SHEARS = ModItems.register("unobtanium_shears", BaseTool::new, new Settings().component(DataComponentTypes.TOOL, createShearsComponent(ToolMaterials.UNOBTANIUM)).enchantable(30).maxDamage(ToolMaterials.UNOBTANIUM.durability()));
    public static final Item BEDROCK_PICKAXE = ModItems.register("bedrock_pickaxe", BaseTool::new, new Settings().pickaxe(ToolMaterials.BEDROCK, 1f, -2.8f).rarity(Rarity.UNCOMMON));
    public static final Item BEDROCK_AXE = ModItems.register("bedrock_axe", BaseTool::new, new Settings().axe(ToolMaterials.BEDROCK, 5f, -3f).rarity(Rarity.UNCOMMON));
    public static final Item THE_PICKAXE = ModItems.register("the_pickaxe", BaseTool::new, new Settings().pickaxe(ToolMaterials.FISH, 1f, -2.8f).rarity(Rarity.RARE));
    public static final Item THE_AXE = ModItems.register("the_axe", BaseTool::new, new Settings().axe(ToolMaterials.FISH, 5f, -3f).rarity(Rarity.RARE));
    public static final Item THE_SHEARS = ModItems.register("the_shears", BaseTool::new, new Settings().component(DataComponentTypes.TOOL, createShearsComponent(ToolMaterials.FISH)).enchantable(50).maxDamage(ToolMaterials.FISH.durability()).rarity(Rarity.RARE));
    public static final Item FISHAXE = ModItems.register("fishaxe", BaseTool::new, new Settings()
            .component(DataComponentTypes.TOOL, new ToolComponent(List.of(
                    ToolComponent.Rule.ofNeverDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)),
                    ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.PICKAXE_MINEABLE), ToolMaterials.FISH.speed()),
                    ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.AXE_MINEABLE), ToolMaterials.FISH.speed()),
                    ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.SHOVEL_MINEABLE), ToolMaterials.FISH.speed()),
                    ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.HOE_MINEABLE), ToolMaterials.FISH.speed())
            ), ToolMaterials.FISH.speed(), 1, true))
            .component(DataComponentTypes.WEAPON, new WeaponComponent(1, 2.5f))
            .enchantable(50)
            .maxDamage(ToolMaterials.FISH.durability() * 3/2)
            .rarity(Rarity.EPIC)
    );

    public static ToolComponent createShearsComponent(ToolMaterial toolMaterial) {
        final RegistryEntryLookup<Block> registryEntryLookup = Registries.createEntryLookup(Registries.BLOCK);
        final float speed = toolMaterial.speed();

        return new ToolComponent(
                List.of(
                        ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Blocks.COBWEB.getRegistryEntry()), speed),
                        ToolComponent.Rule.of(registryEntryLookup.getOrThrow(BlockTags.LEAVES), speed * 0.75f),
                        ToolComponent.Rule.of(registryEntryLookup.getOrThrow(BlockTags.WOOL), speed * 0.65f),
                        ToolComponent.Rule.of(RegistryEntryList.of(Blocks.VINE.getRegistryEntry(), Blocks.GLOW_LICHEN.getRegistryEntry()), speed * 0.25f)
                ),1, 1, true
        );
    }
}