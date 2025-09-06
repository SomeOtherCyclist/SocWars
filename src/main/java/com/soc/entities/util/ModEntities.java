package com.soc.entities.util;

import com.soc.entities.BWFireballEntity;
import com.soc.entities.BigTntEntity;
import com.soc.entities.EnderBeamEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static void initialise() {
        BigTntEntity.initialise();
        EnderBeamEntity.initialise();
        BWFireballEntity.initialise();
    }

    public static <T extends Entity> EntityType<T> registerType(Identifier id, EntityType.Builder<T> type) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }
}
