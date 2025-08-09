package com.soc.items.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class Armour {
    public static RegistryKey<EquipmentAsset> register(String name) {
        return RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), Identifier.of("socwars:" + name));
    }
}
