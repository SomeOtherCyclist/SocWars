package com.soc.items;

import com.soc.items.util.EffectRecord;
import com.soc.items.util.ModItems;
import com.soc.items.util.RingItem;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class PotionRing extends RingItem {
    private final List<EffectRecord> effects;

    public PotionRing(final Settings settings, final EffectRecord... effects) {
        super(settings);
        this.effects = Arrays.stream(effects).toList();
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(LESSER_SPEED_RING, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(GREATER_SPEED_RING, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(LESSER_JUMP_RING, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(GREATER_JUMP_RING, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(INFLATABLE_IRON_INGOT, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(INVISIBILITY_RING, ItemGroups.TOOLS);
    }

    public static final Item LESSER_SPEED_RING = ModItems.register("lesser_speed_ring", settings -> new PotionRing(settings, new EffectRecord(StatusEffects.SPEED, 0)), new Settings().maxDamage(30 * 20).rarity(Rarity.UNCOMMON));
    public static final Item GREATER_SPEED_RING = ModItems.register("greater_speed_ring", settings -> new PotionRing(settings, new EffectRecord(StatusEffects.SPEED, 1)), new Settings().maxDamage(20 * 20).rarity(Rarity.UNCOMMON));
    public static final Item LESSER_JUMP_RING = ModItems.register("lesser_jump_ring", settings -> new PotionRing(settings, new EffectRecord(StatusEffects.JUMP_BOOST, 1)), new Settings().maxDamage(15 * 20).rarity(Rarity.UNCOMMON));
    public static final Item GREATER_JUMP_RING = ModItems.register("greater_jump_ring", settings -> new PotionRing(settings, new EffectRecord(StatusEffects.JUMP_BOOST, 3)), new Settings().maxDamage(5 * 20).rarity(Rarity.UNCOMMON));
    public static final Item INFLATABLE_IRON_INGOT = ModItems.register("inflatable_iron_ingot", settings -> new PotionRing(settings, new EffectRecord(StatusEffects.LEVITATION, 1), new EffectRecord(StatusEffects.SLOW_FALLING, 0, 5)), new Settings().maxDamage(20 * 20).rarity(Rarity.EPIC));
    public static final Item INVISIBILITY_RING = ModItems.register("invisibility_ring", settings -> new PotionRing(settings, new EffectRecord(StatusEffects.INVISIBILITY, 1)), new Settings().maxDamage(20 * 20).rarity(Rarity.EPIC));

    @Override
    protected void ringUse(LivingEntity user) {
        this.effects.forEach(effect -> user.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration() > 0 ? effect.duration() : GRACE_TICKS, effect.amplifier(), false, false, false)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        switch (stack.getItem().toString()) {
            case "socwars:inflatable_iron_ingot" -> textConsumer.accept(Text.translatable("tooltip.inflatable_iron_ingot"));
        }
    }
}
