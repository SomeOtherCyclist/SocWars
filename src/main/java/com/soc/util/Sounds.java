package com.soc.util;

import com.soc.SocWars;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public interface Sounds {
    static void initialise() {}

    private static SoundEvent register(String id) {
        return Registry.register(Registries.SOUND_EVENT, Identifier.of(SocWars.MOD_ID, id), SoundEvent.of(Identifier.of(SocWars.MOD_ID, id)));
    }

    SoundEvent VINE_BOOM = register("vine_boom");
}
