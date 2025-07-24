package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.tools.ToolMaterials;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.LocalRandom;

import static com.soc.items.util.ModItems.addItemToGroups;

public class Lifethief extends Item {
    public Lifethief(Settings settings) {
        super(settings);
    }

    static LocalRandom random = new LocalRandom(0);

    public static void initialise() {
        addItemToGroups(LIFETHIEF, ItemGroups.COMBAT);
    }

    public static final Item LIFETHIEF = ModItems.register("lifethief", Lifethief::new, new Settings()
            .useCooldown(3.5f)
            .sword(ToolMaterials.LIFETHIEF_TOOL_MATERIAL, 2.5f, -2.2f)
            .component(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(false, ReferenceSortedSets.emptySet()))
    );

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attacker.setHealth(Math.min(attacker.getMaxHealth(), attacker.getHealth() + random.nextBetween(-1, 2)));
    }
}
