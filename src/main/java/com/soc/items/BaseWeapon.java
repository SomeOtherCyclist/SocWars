package com.soc.items;

import com.soc.SocWars;
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

import static com.soc.items.util.ModItems.addItemToGroups;

public class BaseWeapon extends Item {
    public BaseWeapon(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        ModItems.addItemToGroups(BAT, ItemGroups.COMBAT);
        ModItems.addItemToGroups(BARBED_WIRE_BAT, ItemGroups.COMBAT);
        ModItems.addItemToGroups(NAILED_BAT, ItemGroups.COMBAT);
        ModItems.addItemToGroups(RAZOR_WIRE_BAT, ItemGroups.COMBAT);
        ModItems.addItemToGroups(SAW_BLADE_BAT, ItemGroups.COMBAT);
        ModItems.addItemToGroups(DEVASTATOR, ItemGroups.COMBAT);
        ModItems.addItemToGroups(NETHERIGHT_SWORD, ItemGroups.COMBAT);
    }

    public static final Item BAT = ModItems.register("bat", BaseWeapon::new, new Settings().sword(ToolMaterials.BASE, 4f, -1.9f));
    public static final Item BARBED_WIRE_BAT = ModItems.register("barbed_wire_bat", BaseWeapon::new, new Settings().sword(ToolMaterials.BASE, 5f, -1.95f));
    public static final Item NAILED_BAT = ModItems.register("nailed_bat", BaseWeapon::new, new Settings().sword(ToolMaterials.BASE, 6f, -2f).rarity(Rarity.UNCOMMON));
    public static final Item RAZOR_WIRE_BAT = ModItems.register("razor_wire_bat", BaseWeapon::new, new Settings().sword(ToolMaterials.BASE, 7.5f, -2.1f).rarity(Rarity.RARE));
    public static final Item SAW_BLADE_BAT = ModItems.register("saw_blade_bat", BaseWeapon::new, new Settings().sword(ToolMaterials.BASE, 9f, -2.2f).rarity(Rarity.RARE));
    public static final Item DEVASTATOR = ModItems.register("devastator", BaseWeapon::new, new Settings().rarity(Rarity.RARE).sword(ToolMaterials.DEVASTATOR, 2f, -3.7f));
    public static final Item NETHERIGHT_SWORD = ModItems.register("netheright_sword", BaseWeapon::new, new Settings().sword(ToolMaterial.NETHERITE, -4.5f, 12f));

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        Text text = switch (stack.getItem().toString()) {
            case "socwars:bat" -> Text.literal("*hits you cutely*");
            case "socwars:saw_blade_bat" -> Text.literal("Last day").formatted(Formatting.DARK_RED);
            default -> null;
        };

        SocWars.LOGGER.info(stack.getItem().toString());

        if (text != null) {
            textConsumer.accept(text);
        }
    }
}
