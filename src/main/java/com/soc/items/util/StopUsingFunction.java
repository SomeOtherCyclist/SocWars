package com.soc.items.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface StopUsingFunction {
    boolean stopUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks);
}
