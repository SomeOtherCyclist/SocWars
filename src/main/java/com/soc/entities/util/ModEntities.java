package com.soc.entities.util;

import com.soc.SocWars;
import com.soc.entities.*;
import com.soc.game.manager.bedwars.ShopType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface ModEntities {
    static void initialise() {}

    EntityType<BWFireballEntity> FIREBALL = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "fireball"), EntityType.Builder.<BWFireballEntity>create((type, world) -> new BWFireballEntity(type, world, 4f, BWFireballEntity::fireballExplosion), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.25f, 0.25f)
            .maxTrackingRange(4)
            .trackingTickInterval(1)
    );
    EntityType<BWFireballEntity> WATERBALL = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "waterball"), EntityType.Builder.<BWFireballEntity>create((type, world) -> new BWFireballEntity(type, world, 0f, BWFireballEntity::waterballExplosion), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.25f, 0.25f)
            .maxTrackingRange(4)
            .trackingTickInterval(1)
    );
    EntityType<BWFireballEntity> SNAIL_FIREBALL = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "snail_fireball"), EntityType.Builder.<BWFireballEntity>create((type, world) -> new BWFireballEntity(type, world, 20f, BWFireballEntity::snailExplosion), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.25f, 0.25f)
            .maxTrackingRange(4)
            .trackingTickInterval(1)
    );
    EntityType<BWFireballEntity> LIGHTNING_ORB = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "lighting_orb"), EntityType.Builder.<BWFireballEntity>create((type, world) -> new BWFireballEntity(type, world, 0f, BWFireballEntity::lightningOrbExplosion), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.25f, 0.25f)
            .maxTrackingRange(4)
            .trackingTickInterval(1)
    );
    EntityType<BigTntEntity> NUCLEAR_BOMB = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "nuclear_bomb"), EntityType.Builder.<BigTntEntity>create((type, world) -> new BigTntEntity(type, world, BigTntEntity.BigTntType.NUCLEAR), SpawnGroup.MISC)
            .dropsNothing()
            .makeFireImmune()
            .dimensions(0.98f, 0.98f)
            .eyeHeight(0.15f)
            .maxTrackingRange(10)
            .trackingTickInterval(10)
    );
    EntityType<BigTntEntity> HYDROGEN_BOMB = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "hydrogen_bomb"), EntityType.Builder.<BigTntEntity>create((type, world) -> new BigTntEntity(type, world, BigTntEntity.BigTntType.HYDROGEN), SpawnGroup.MISC)
            .dropsNothing()
            .makeFireImmune()
            .dimensions(0.98f, 0.98f)
            .eyeHeight(0.15f)
            .maxTrackingRange(10)
            .trackingTickInterval(10)
    );
    EntityType<EnderBeamEntity> ENDER_BEAM = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "ender_beam"), EntityType.Builder.<EnderBeamEntity>create(EnderBeamEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.25f, 0.25f)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
    );
    EntityType<BedwarsShopEntity> INDIVIDUAL_BEDWARS_SHOP = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "individual_bedwars_shop"), EntityType.Builder.<BedwarsShopEntity>create((type, world) -> new BedwarsShopEntity(type, world, ShopType.INDIVIDUAL), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.6f, 1.95f)
            .maxTrackingRange(4)
            .eyeHeight(1.62f)
    );
    EntityType<BedwarsShopEntity> TEAM_BEDWARS_SHOP = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "team_bedwars_shop"), EntityType.Builder.<BedwarsShopEntity>create((type, world) -> new BedwarsShopEntity(type, world, ShopType.TEAM), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.6f, 1.95f)
            .maxTrackingRange(4)
            .eyeHeight(1.62f)
    );
    EntityType<HandGrenadeEntity> HAND_GRENADE = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "hand_grenade"), EntityType.Builder.<HandGrenadeEntity>create((type, world) -> new HandGrenadeEntity(type, world, 0.5f), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.5f, 0.625f)
            .maxTrackingRange(4)
            .eyeHeight(0.6f)
    );
    EntityType<HolyHandGrenadeEntity> HOLY_HAND_GRENADE = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "holy_hand_grenade"), EntityType.Builder.<HolyHandGrenadeEntity>create((type, world) -> new HolyHandGrenadeEntity(type, world, 0.5f), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.5f, 0.85f)
            .maxTrackingRange(4)
            .eyeHeight(0.6f)
    );
    EntityType<MolotovCocktailEntity> MOLOTOV_COCKTAIL = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "molotov_cocktail"), EntityType.Builder.<MolotovCocktailEntity>create(MolotovCocktailEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.2f, 0.5f)
            .maxTrackingRange(4)
            .eyeHeight(0.4f)
    );
    EntityType<RedShellEntity> RED_SHELL = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "red_shell"), EntityType.Builder.<RedShellEntity>create(RedShellEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.2f, 0.2f)
            .maxTrackingRange(8)
            .eyeHeight(0.1f)
    );
    EntityType<BlueShellEntity> BLUE_SHELL = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "blue_shell"), EntityType.Builder.<BlueShellEntity>create(BlueShellEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.2f, 0.2f)
            .maxTrackingRange(8)
            .eyeHeight(0.1f)
    );
    EntityType<PocketSandEntity> POCKET_SAND = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "pocket_sand"), EntityType.Builder.create(PocketSandEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.3f, 0.3f)
            .maxTrackingRange(8)
            .eyeHeight(0.1f)
    );
    EntityType<JetShoppingTrolleyEntity> JET_SHOPPING_TROLLEY = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "jet_shopping_trolley"), EntityType.Builder.<JetShoppingTrolleyEntity>create(JetShoppingTrolleyEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.7f, 0.7f)
            .maxTrackingRange(8)
            .eyeHeight(0.1f)
    );
    EntityType<PowerupEntity> POWERUP = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "powerup"), EntityType.Builder.<PowerupEntity>create(PowerupEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.75f, 1f)
            .maxTrackingRange(4)
            .eyeHeight(0.5f)
    );

    static <T extends Entity> EntityType<T> registerType(Identifier id, EntityType.Builder<T> type) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }
}
