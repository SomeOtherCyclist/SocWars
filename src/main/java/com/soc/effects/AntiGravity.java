package com.soc.effects;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.soc.SocWars;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class AntiGravity extends StatusEffect {
    private PlayerEntity playerEntity;

    protected AntiGravity() {
        super(StatusEffectCategory.BENEFICIAL, 0xfffdec);
    }

    public static void initialise() {}

    public static final RegistryEntry<StatusEffect> ANTI_GRAVITY =
            Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SocWars.MOD_ID, "anti_gravity"), new AntiGravity());

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        float gravity = (float) entity.getAttributeValue(EntityAttributes.GRAVITY);
        Objects.requireNonNull(entity.getAttributeInstance(EntityAttributes.GRAVITY)).addTemporaryModifier(new EntityAttributeModifier(Identifier.of("gravity_orb"), -(11 + amplifier) * 0.1f * gravity, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = ImmutableMultimap.of(EntityAttributes.GRAVITY, new EntityAttributeModifier(Identifier.of("gravity_orb"), 0, EntityAttributeModifier.Operation.ADD_VALUE));
        attributeContainer.removeModifiers(map);
    }
}
