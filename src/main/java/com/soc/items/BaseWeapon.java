package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.materials.ToolMaterials;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import java.util.function.Consumer;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class BaseWeapon extends Item {

    public BaseWeapon(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(BAT, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(BARBED_WIRE_BAT, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(NAILED_BAT, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(RAZOR_WIRE_BAT, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(SAW_BLADE_BAT, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(DEVASTATOR, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(NETHERIGHT_SWORD, ItemGroups.COMBAT);
    }

    public static final Item BAT = ModItems.register("bat", BaseWeapon::new, new Settings().sword(ToolMaterials.BASE, 4f, -1.9f));
    public static final Item BARBED_WIRE_BAT = ModItems.register("barbed_wire_bat", BaseWeapon::new, new Settings().sword(ToolMaterials.BASE, 5f, -1.95f));
    public static final Item NAILED_BAT = ModItems.register("nailed_bat", BaseWeapon::new, new Settings().sword(ToolMaterials.BASE, 6f, -2f).rarity(Rarity.UNCOMMON));
    public static final Item RAZOR_WIRE_BAT = ModItems.register("razor_wire_bat", BaseWeapon::new, new Settings().sword(ToolMaterials.BASE, 7.5f, -2.1f).rarity(Rarity.RARE));
    public static final Item SAW_BLADE_BAT = ModItems.register("saw_blade_bat", BaseWeapon::new, new Settings().sword(ToolMaterials.BASE, 9f, -2.4f).rarity(Rarity.RARE));
    public static final Item DEVASTATOR = ModItems.register("devastator", BaseWeapon::new, new Settings().rarity(Rarity.RARE).sword(ToolMaterials.DEVASTATOR, 4f, -3.5f));
    public static final Item NETHERIGHT_SWORD = ModItems.register("netheright_sword", BaseWeapon::new, new Settings().sword(ToolMaterial.NETHERITE, -4.5f, 12f));

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        switch (stack.getItem().toString()) {
            case "socwars:bat" -> textConsumer.accept(Text.translatable("tooltip.bat"));
            case "socwars:saw_blade_bat" -> textConsumer.accept(Text.translatable("tooltip.saw_blade_bat").formatted(Formatting.DARK_RED));
        }
    }
}
