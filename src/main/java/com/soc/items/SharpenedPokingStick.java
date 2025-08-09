package com.soc.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.soc.items.util.ModItems;
import com.soc.util.Random;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class SharpenedPokingStick extends Item {

    public SharpenedPokingStick(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        ModItems.addItemToGroups(SHARPENED_POKING_STICK, ItemGroups.COMBAT);
    }

    public static final Item SHARPENED_POKING_STICK = ModItems.register("sharpened_poking_stick", SharpenedPokingStick::new, new Settings().maxDamage(150).rarity(Rarity.UNCOMMON));

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof PlayerEntity playerEntity) {
            Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = ImmutableMultimap.of(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.ofVanilla("stick_reach"), 3, EntityAttributeModifier.Operation.ADD_VALUE), EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(Identifier.ofVanilla("stick_damage"), 5, EntityAttributeModifier.Operation.ADD_VALUE), EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(Identifier.ofVanilla("stick_attack_speed"), 0.2, EntityAttributeModifier.Operation.ADD_VALUE));
            playerEntity.getAttributes().removeModifiers(map);

            if (slot != null && slot.getIndex() == 0) {
                playerEntity.getAttributes().addTemporaryModifiers(map);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {

        textConsumer.accept(Text.empty());
        textConsumer.accept(Text.translatable("item.modifiers.mainhand").formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("attack_damage", new Object[]{6}).formatted(Formatting.DARK_GREEN));
        textConsumer.accept(Text.translatable("attack_speed", new Object[]{1.8f}).formatted(Formatting.DARK_GREEN));
        textConsumer.accept(Text.translatable("reach", new Object[]{3}).formatted(Formatting.DARK_GREEN));
    }
}
