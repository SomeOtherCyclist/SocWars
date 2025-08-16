package com.soc.items.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface AttackFunction {
    void attack(ItemStack stack, LivingEntity target, LivingEntity attacker);
}
