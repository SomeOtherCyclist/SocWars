package com.soc.items.components;

import com.mojang.serialization.Codec;
import com.soc.SocWars;
import com.soc.game.map.ResourceGenerator;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

import java.util.UUID;

public interface ModComponents {
    ComponentType<RingItemComponent> RING_ITEM_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "ring_item"),
            ComponentType.<RingItemComponent>builder().codec(RingItemComponent.CODEC).build()
    );
    ComponentType<Integer> TRAINING_WEIGHTS_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "training_weights"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );
    ComponentType<ExponComponent> EXPON_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "expon"),
            ComponentType.<ExponComponent>builder().codec(ExponComponent.CODEC).build()
    );
    ComponentType<Unit> RESOURCE_COUNTED = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "resource_counted"),
            ComponentType.<Unit>builder().codec(Codec.unit(Unit.INSTANCE)).build()
    );
    ComponentType<Unit> RESOURCE_SPLIT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "resource_split"),
            ComponentType.<Unit>builder().codec(Codec.unit(Unit.INSTANCE)).build()
    );
    ComponentType<Integer> GAME_TOOL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "game_tool"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );
    ComponentType<Boolean> DOUBLE_JUMP = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "double_jump"),
            ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );
    ComponentType<Integer> GENERATOR_REFERENCE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "generator_reference"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );
    ComponentType<CommandFunctionComponent> COMMAND_FUNCTION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "command_function"),
            ComponentType.<CommandFunctionComponent>builder().codec(CommandFunctionComponent.CODEC).build()
    );

    static void initialise() {}
}
