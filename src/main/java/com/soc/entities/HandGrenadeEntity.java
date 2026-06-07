package com.soc.entities;

import com.soc.util.DamageTypes;
import com.soc.util.SphereExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HandGrenadeEntity extends ThrownEntity {
    protected float detonationTime;

    private float detonationTimer = -1f;

    public HandGrenadeEntity(EntityType<? extends ThrownEntity> type, World world, float detonationTime) {
        super(type, world);
        this.detonationTime = detonationTime;
    }

    public HandGrenadeEntity(EntityType<? extends ThrownEntity> type, World world, float detonationTime, Entity owner) {
        super(type, world);
        this.detonationTime = detonationTime;

        this.setOwner(owner);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.detonationTime = view.getFloat("detonation_time", 0.5f);
        this.detonationTimer = view.getFloat("detonation_timer", -1f);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putFloat("detonation_time", this.detonationTime);
        view.putFloat("detonation_timer", this.detonationTimer);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        final Entity other = entityHitResult.getEntity();
        final Entity owner = this.getOwner();
        if (other == owner || other.isTeammate(owner)) return;

        super.onEntityHit(entityHitResult);
        this.trigger();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.trigger();
    }

    private void trigger() {
        if (this.detonationTimer < -0.1f) {
            this.detonationTimer = 0f;
        }
    }

    public float getDetonationTimer() {
        return Math.max(0f, this.detonationTimer);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.detonationTimer > -0.1f) {
            this.detonationTimer += 0.05f;
            if (this.detonationTimer > this.detonationTime) {
                this.detonate();
                this.discard();
            }
        }
    }

    protected void detonate() {
        SphereExplosion.explode(this.getWorld(), this.getPos(), 3f, 1.5f, 12f, 3f, true, this.getOwner(), DamageTypes.SPHERE_EXPLOSION);
    }
}
