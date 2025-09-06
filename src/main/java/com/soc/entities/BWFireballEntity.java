package com.soc.entities;

import com.soc.SocWars;
import com.soc.entities.util.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class BWFireballEntity extends FireballEntity {
    public static class BWFireballBehaviour extends ExplosionBehavior {
        @Override
        public float calculateDamage(Explosion explosion, Entity entity, float amount) {
            return super.calculateDamage(explosion, entity, amount) * 0.125f;
        }

        @Override
        public float getKnockbackModifier(Entity entity) {
            return 1.25f;
        }
    }

    private final float explosionPower;
    private final LivingEntity attacker;

    { this.accelerationPower = 0f; }

    public BWFireballEntity(EntityType<? extends BWFireballEntity> entityType, World world) {
        super(entityType, world);
        this.explosionPower = 0;
        this.attacker = null;
    }

    public BWFireballEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
        super(BW_FIREBALL_TYPE, world);
        this.setVelocity(velocity);
        this.explosionPower = explosionPower;
        this.attacker = owner;
    }

    public static void initialise() {}

    public static final EntityType<BWFireballEntity> BW_FIREBALL_TYPE = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "bw_fireball"), EntityType.Builder.<BWFireballEntity>create(BWFireballEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.25F, 0.25F)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
    );

    @Override
    public float getDrag() {
        return 1f;
    }

    private DamageSource damageSource() {
        return new DamageSource(this.getWorld().getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(DamageTypes.EXPLOSION), this.attacker, this.attacker);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.createExplosion(
                    this,
                    this.damageSource(),
                    new BWFireballBehaviour(),
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    this.explosionPower,
                    true,
                    World.ExplosionSourceType.TNT
            );
            this.discard();
        }
    }
}

