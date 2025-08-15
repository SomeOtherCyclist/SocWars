package com.soc.items.util;

import com.soc.SocWars;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface DamageTypes {
    RegistryKey<DamageType> LIFETHIEF = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SocWars.MOD_ID, "lifethief"));
    RegistryKey<DamageType> NETHERWRONG_SWORD = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SocWars.MOD_ID, "netherwrong_sword_damage"));
}
