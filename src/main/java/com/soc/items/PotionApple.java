package com.soc.items;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.soc.SocWars;
import com.soc.items.util.EffectRecord;
import com.soc.items.util.ModItems;
import com.soc.effects.Armour;
import net.minecraft.component.type.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.function.Consumer;

import static com.soc.items.util.ModItems.addItemToGroups;
import static net.minecraft.component.type.PotionContentsComponent.getEffectText;

public class PotionApple extends Item {
    private static FoodComponent foodComponent(int nutrition, int saturation) {
        return new FoodComponent.Builder()
            .nutrition(nutrition)
            .saturationModifier((float) saturation / (nutrition * 2))
            .alwaysEdible()
            .build();
    }

    private static ConsumableComponent consumableComponent(EffectRecord[] effectRecords) {
        ConsumableComponent.Builder consumableComponent = ConsumableComponents.food();

        for (EffectRecord effectRecord : effectRecords) {
            consumableComponent.consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(effectRecord.effect(), effectRecord.duration(), effectRecord.amplifier()), 1.0f));
        }

        return consumableComponent.build();
    }

    private final static EffectRecord[] IRON_APPLE_EFFECTS = {
            new EffectRecord(Armour.ARMOUR, 5, 60 * 20),
    };
    private final static EffectRecord[] COPPER_APPLE_EFFECTS = {
            new EffectRecord(StatusEffects.RESISTANCE, 0, 60 * 20),
            new EffectRecord(StatusEffects.ABSORPTION, 0, -1),
    };
    private final static EffectRecord[] DIAMOND_APPLE_EFFECTS = {
            new EffectRecord(StatusEffects.RESISTANCE, 1, 60 * 20),
            new EffectRecord(StatusEffects.STRENGTH, 0, 60 * 20),
            new EffectRecord(StatusEffects.INSTANT_HEALTH, 1, 1),
            new EffectRecord(StatusEffects.ABSORPTION, 2, -1),
    };
    private final static EffectRecord[] EMERALD_APPLE_EFFECTS = {

    };
    private final static EffectRecord[] NETHERITE_APPLE_EFFECTS = {
            new EffectRecord(StatusEffects.RESISTANCE, 1, 60 * 20),
            new EffectRecord(StatusEffects.STRENGTH, 1, 120 * 20),
            new EffectRecord(StatusEffects.REGENERATION, 1, 60 * 20),
            new EffectRecord(StatusEffects.INSTANT_HEALTH, 100, 1), //Entity.applyInstantEffect?
            new EffectRecord(StatusEffects.ABSORPTION, 3, -1),
    };
    private final EffectRecord[] effectList;

    public PotionApple(final Item.Settings settings, EffectRecord[] effectRecords, int nutrition, int saturation) {
        super(settings.food(foodComponent(nutrition, saturation), consumableComponent(effectRecords)));
        this.effectList = effectRecords;
    }

    public static void initialise() {
        addItemToGroups(IRON_APPLE, ItemGroups.FOOD_AND_DRINK);
        addItemToGroups(COPPER_APPLE, ItemGroups.FOOD_AND_DRINK);
        addItemToGroups(DIAMOND_APPLE, ItemGroups.FOOD_AND_DRINK);
        addItemToGroups(EMERALD_APPLE, ItemGroups.FOOD_AND_DRINK);
        addItemToGroups(NETHERITE_APPLE, ItemGroups.FOOD_AND_DRINK);
    }

    public static final Item IRON_APPLE = ModItems.register("iron_apple", (settings) -> new PotionApple(settings, IRON_APPLE_EFFECTS, 6, 10), new Settings());
    public static final Item COPPER_APPLE = ModItems.register("copper_apple", (settings) -> new PotionApple(settings, COPPER_APPLE_EFFECTS, 6, 10), new Settings());
    public static final Item DIAMOND_APPLE = ModItems.register("diamond_apple", (settings) -> new PotionApple(settings, DIAMOND_APPLE_EFFECTS, 6, 12), new Settings().rarity(Rarity.RARE));
    public static final Item EMERALD_APPLE = ModItems.register("emerald_apple", (settings) -> new PotionApple(settings, EMERALD_APPLE_EFFECTS, 6, 12), new Settings().rarity(Rarity.RARE));
    public static final Item NETHERITE_APPLE = ModItems.register("netherite_apple", (settings) -> new PotionApple(settings, NETHERITE_APPLE_EFFECTS, 6, 16), new Settings().rarity(Rarity.EPIC));

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        List<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> list = Lists.newArrayList();

        SocWars.LOGGER.warn("Rewrite this code because it was derived from minecraft code if I ever want to release this");

        for(EffectRecord effectRecord : this.effectList) {
            RegistryEntry<StatusEffect> registryEntry = effectRecord.effect();
            int i = effectRecord.amplifier();
            (registryEntry.value()).forEachAttributeModifier(i, (attribute, modifier) -> list.add(new Pair<>(attribute, modifier)));
            MutableText mutableText = Text.translatable("potion_apple.with_duration", new Object[]{getEffectText(registryEntry, i), effectRecord.duration() > 1 ? "(" + effectRecord.duration() / 20 + "s)": ""});

            textConsumer.accept(mutableText.formatted(Formatting.GREEN));
        }

        textConsumer.accept(Text.empty());
        textConsumer.accept(Text.translatable("potion_apple.when_eaten").formatted(Formatting.DARK_PURPLE));

        for(Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier> pair : list) {
            EntityAttributeModifier entityAttributeModifier = pair.getSecond();

            double e = entityAttributeModifier.value() * (entityAttributeModifier.operation() == EntityAttributeModifier.Operation.ADD_VALUE ? 1.0f : 100.0f);

            textConsumer.accept(Text.translatable("attribute.modifier.plus." + entityAttributeModifier.operation().getId(), new Object[]{AttributeModifiersComponent.DECIMAL_FORMAT.format(e), Text.translatable(((pair.getFirst()).value()).getTranslationKey())}).formatted(Formatting.BLUE));
        }
    }
}
