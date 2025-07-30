package com.soc.items.util;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public record EffectRecord(RegistryEntry<StatusEffect> effect, int amplifier, int duration, int reciprocalChance) {
    public EffectRecord(RegistryEntry<StatusEffect> effect, int amplifier, int duration) {
        this(effect, amplifier, duration, 1);
    }

    public EffectRecord(RegistryEntry<StatusEffect> effect, int amplifier) {
        this(effect, amplifier, 0, 1);
    }
}