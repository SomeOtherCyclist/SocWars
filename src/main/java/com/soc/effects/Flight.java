package com.soc.effects;

import com.soc.SocWars;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class Flight extends StatusEffect {
    private PlayerEntity playerEntity;

    protected Flight() {
        super(StatusEffectCategory.BENEFICIAL, 0xfffdec);
    }

    public static void initialise() {}

    public static final RegistryEntry<StatusEffect> FLIGHT =
            Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SocWars.MOD_ID, "flight"), new Flight());

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity playerEntity) {
            this.playerEntity = playerEntity;
            playerEntity.getAbilities().allowFlying = true;
            playerEntity.sendAbilitiesUpdate();
        }
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        playerEntity.getAbilities().allowFlying = playerEntity.isCreative();
        if (!playerEntity.isCreative()) playerEntity.getAbilities().flying = false;
        playerEntity.sendAbilitiesUpdate();
    }
}
