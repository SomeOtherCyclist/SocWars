package com.soc.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class BigTnt extends Entity {
    private final int explosionPower;
    private final boolean createFire;

    public BigTnt(EntityType<?> type, World world, int explosionPower, boolean createFire) {
        super(type, world);
        this.explosionPower = explosionPower;
        this.createFire = createFire;
    }

    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        world.createExplosion(null, Explosion.createDamageSource(world, null), null, user.getX(), user.getY() - 2, user.getZ(), this.explosionPower, this.createFire, World.ExplosionSourceType.TNT);

        return ActionResult.SUCCESS;
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

    }

    @Override
    protected void writeCustomData(WriteView view) {

    }
}
