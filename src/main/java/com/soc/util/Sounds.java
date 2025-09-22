package com.soc.util;

import com.soc.SocWars;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public interface Sounds {
    static void initialise() {}

    SoundEvent VINE_BOOM = register("vine_boom");
    SoundEvent FLESH = register("flesh");
    SoundEvent NUCLEAR_SIREN = register("nuclear_siren");
    SoundEvent GIGA_CHAD = register("giga_chad");
    SoundEvent AIR_HORN = register("air_horn");

    private static SoundEvent register(String id) {
        return Registry.register(Registries.SOUND_EVENT, Identifier.of(SocWars.MOD_ID, id), SoundEvent.of(Identifier.of(SocWars.MOD_ID, id)));
    }
}
