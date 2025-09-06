package com.soc.entities;

import com.soc.SocWars;
import com.soc.entities.util.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class EnderBeamEntity extends EnderPearlEntity {

    public EnderBeamEntity(EntityType<? extends EnderBeamEntity> entityType, World world) {
        super(entityType, world);
    }

    public static void initialise() {}

    public static final EntityType<EnderBeamEntity> ENDER_BEAM_TYPE = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "ender_beam"), EntityType.Builder.create(EnderBeamEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.25F, 0.25F)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
    );

    @Override
    protected double getGravity() {
        return 0;
    }
}

