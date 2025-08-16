package com.soc.items.util;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface ModifyEquipmentFunction {
    void modifyEquipment(LivingEntity target, EquipmentSlot slot);
}
