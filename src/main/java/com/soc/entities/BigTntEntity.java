package com.soc.entities;

import com.soc.util.SphereExplosion;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.soc.entities.util.ModEntities.HYDROGEN_BOMB;
import static com.soc.entities.util.ModEntities.NUCLEAR_BOMB;

public class BigTntEntity extends Entity implements Ownable {
    public enum BigTntType {
        NUCLEAR(10.5f, 4 * 20),
        HYDROGEN(15f, 6 * 20);

        public final float explosionRadius;
        public final int fuse;

        BigTntType(float explosionRadius, int fuse) {
            this.explosionRadius = explosionRadius;
            this.fuse = fuse;
        }
    }

    private static final TrackedData<Integer> FUSE = DataTracker.registerData(BigTntEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private BigTntType tntType;
    private LazyEntityReference<LivingEntity> igniter;

    public BigTntEntity(EntityType<BigTntEntity> entityType, World world) {
        super(entityType, world);
        this.tntType = BigTntType.NUCLEAR;
        //this.intersectionChecked = true;
    }

    public BigTntEntity(EntityType<BigTntEntity> entityType, World world, BigTntType type) {
        super(entityType, world);
        this.tntType = type;
        //this.intersectionChecked = true;
    }

    public BigTntEntity(World world, Vec3d position, @Nullable LivingEntity igniter, BigTntType tntType) {
        this(switch (tntType) {
            case NUCLEAR -> NUCLEAR_BOMB;
            case HYDROGEN -> HYDROGEN_BOMB;
        }, world);
        
        this.setPosition(position);
        this.setVelocity(new Vec3d(0, 0, 0));
        
        this.tntType = tntType;
        this.setFuse(tntType.fuse);
        if (igniter != null) this.igniter = new LazyEntityReference<>(igniter);
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(FUSE, 10 * 20);
    }

    protected double getGravity() {
        return 0.04;
    }

    public void tick() {
        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.tickBlockCollision();
        this.setVelocity(this.getVelocity().multiply(0.97));

        super.tick();

        if (getFuse() > 0) {
            setFuse(getFuse() - 1);
            return;
        }

        this.explode();
        this.discard();
    }

    private void explode() {
        SphereExplosion.explode(this.getWorld(), this.getPos(), this.tntType.explosionRadius, 2f, 10f, 2f, true, LazyEntityReference.resolve(this.igniter, this.getWorld(), LivingEntity.class), null);
    }

    protected void writeCustomData(WriteView view) {
        view.putInt("fuse", this.getFuse());

        if (this.igniter != null) LazyEntityReference.writeData(this.igniter, view, "igniter");
    }

    protected void readCustomData(ReadView view) {
        this.setFuse(view.getInt("fuse", 10 * 20));
        this.igniter = LazyEntityReference.fromData(view, "igniter");
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return LazyEntityReference.resolve(this.igniter, this.getWorld(), LivingEntity.class);
    }

    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }

    public BigTntType getTntType() {
        return this.tntType;
    }

    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }
}

