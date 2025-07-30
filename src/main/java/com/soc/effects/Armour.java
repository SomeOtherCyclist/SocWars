package com.soc.effects;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.soc.SocWars;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class Armour extends StatusEffect {
    protected Armour() {
        super(StatusEffectCategory.BENEFICIAL, 0xbabbc7);
    }

    public static void initialise() {}

    public static final RegistryEntry<StatusEffect> ARMOUR =
            Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SocWars.MOD_ID, "armour"), new Armour());

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            Objects.requireNonNull(entity.getAttributeInstance(EntityAttributes.ARMOR)).addTemporaryModifier(new EntityAttributeModifier(Identifier.ofVanilla("apple_armour"), amplifier + 1, EntityAttributeModifier.Operation.ADD_VALUE));
        }
        super.onApplied(entity, amplifier);
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = ImmutableMultimap.of(EntityAttributes.ARMOR, new EntityAttributeModifier(Identifier.ofVanilla("apple_armour"), 0, EntityAttributeModifier.Operation.ADD_VALUE));
        attributeContainer.removeModifiers(map);

        super.onRemoved(attributeContainer);
    }
}