package com.soc.items;

import com.soc.SocWars;
import com.soc.items.components.ModComponents;
import com.soc.items.util.AppendTooltipFunction;
import com.soc.items.util.ItemGroups;
import com.soc.items.util.ModItems;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Consumer;

public class BaseItem extends Item {
    private final AppendTooltipFunction tooltipFunction;

    public BaseItem(Settings settings, AppendTooltipFunction tooltipFunction) {
        super(settings);
        this.tooltipFunction = tooltipFunction;
    }

    public BaseItem(Settings settings, Text simpleTooltip) {
        this(settings, (a, b) -> b.accept(simpleTooltip));
    }

    public BaseItem(Settings settings) {
        this(settings, (a, b) -> {});
    }

    public static void initialise() {
        ItemGroups.addItemToItemsGroup(SHARPENED_POKING_STICK);
        ItemGroups.addItemToItemsGroup(BALUSTRADE);
    }

    public static final Item SHARPENED_POKING_STICK = ModItems.register("sharpened_poking_stick", BaseItem::new, new Settings().maxDamage(150).rarity(Rarity.UNCOMMON).attributeModifiers(AttributeModifiersComponent.builder()
            .add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "stick_reach"), 1.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
            .add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "stick_damage"), 5, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
            .add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "stick_attack_speed"), 0.2, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
            .build()
    ));
    public static final Item BALUSTRADE = ModItems.register("balustrade", Item::new, new Settings());

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        this.tooltipFunction.appendTooltip(stack, textConsumer);
    }
}
