package com.soc.entities;

import com.soc.util.DamageTypes;
import com.soc.util.SphereExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.world.World;

public class HolyHandGrenadeEntity extends HandGrenadeEntity {
    public HolyHandGrenadeEntity(EntityType<? extends ThrownEntity> type, World world, float detonationTime) {
        super(type, world, detonationTime);
    }

    public HolyHandGrenadeEntity(EntityType<? extends ThrownEntity> type, World world, float detonationTime, Entity owner) {
        super(type, world, detonationTime, owner);
    }

    @Override
    protected void detonate() {
        SphereExplosion.explode(this.getWorld(), this.getPos(), 8f, 1.5f, 15f, 1f, false, this.getOwner(), DamageTypes.HOLY_HAND_GRENADE);
    }
}
