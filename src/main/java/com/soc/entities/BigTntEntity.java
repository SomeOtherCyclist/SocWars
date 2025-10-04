package com.soc.entities;

import com.soc.SocWars;
import com.soc.entities.util.ModEntities;
import com.soc.util.SphereExplosion;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
/*
public class BigTnt extends Entity {
    private final int explosionRadius;
    private final boolean createFire;

    private int fuseTime;

    public BigTnt(EntityType<?> type, World world, int explosionRadius, boolean createFire, int fuseTime) {
        super(type, world);
        this.explosionRadius = explosionRadius;
        this.createFire = createFire;
        this.fuseTime = fuseTime;
    }

    public static void initialise() {
        FabricDefaultAttributeRegistry.register(NUCLEAR_BOMB, BigTnt.createMobAttributes());
    }

    public class CubeEntityRenderer extends MobEntityRenderer<BigTnt, CubeEntityModel> {

        public CubeEntityRenderer(EntityRendererFactory.Context context) {
            super(context, new CubeEntityModel(context.getPart(SocWarsClient.MODEL_CUBE_LAYER)), 0.5f);
        }

        @Override
        public EntityRenderState createRenderState() {
            return null;
        }

        @Override
        public Identifier getTexture(BigTnt entity) {
            return Identifier.of(SocWars.MOD_ID, "textures/entity/cube/cube.png");
        }

        @Override
        public Identifier getTexture(LivingEntityRenderState state) {
            return null;
        }
    }

    public static final EntityType<Entity> NUCLEAR_BOMB = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SocWars.MOD_ID, "nuclear_bomb"),
            EntityType.Builder.create((entityType, world) -> new BigTnt(entityType, world, 11, false, 10 * 20), SpawnGroup.MISC).dropsNothing().makeFireImmune().dimensions(0.98f, 0.98f).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(SocWars.MOD_ID, "nuclear_bomb")))
    );

    @Override
    public void tick() {
        if (fuseTime > 0) {
            fuseTime--;
        } else {
            SphereExplosion.explode(this.getWorld(), this.getBlockPos(), this.explosionRadius);
        }
        super.tick();
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
*/

public class BigTntEntity extends Entity implements Ownable {
    public static class BigTntExplosionBehaviour extends ExplosionBehavior {
        private float knockbackModifier;

        @Override
        public float calculateDamage(Explosion explosion, Entity entity, float amount) {
            return super.calculateDamage(explosion, entity, amount) * 8f;
        }

        @Override
        public float getKnockbackModifier(Entity entity) {
            return knockbackModifier;
        }

        public void setKnockbackModifier(float modifier) {
            this.knockbackModifier = modifier;
        }
    }

    public enum BigTntType {
        NUCLEAR(14f, 10 * 20),
        HYDROGEN(24f, 15 * 20);

        public float explosionRadius;
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

    public static final EntityType<BigTntEntity> NUCLEAR_BOMB = ModEntities.registerType(
            Identifier.of(SocWars.MOD_ID, "nuclear_bomb"),
            EntityType.Builder.<BigTntEntity>create(BigTntEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .makeFireImmune()
                    .dimensions(0.98F, 0.98F)
                    .eyeHeight(0.15F)
                    .maxTrackingRange(10)
                    .trackingTickInterval(10)
    );
    public static final EntityType<BigTntEntity> HYDROGEN_BOMB = ModEntities.registerType(
            Identifier.of(SocWars.MOD_ID, "hydrogen_bomb"),
            EntityType.Builder.<BigTntEntity>create(BigTntEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .makeFireImmune()
                    .dimensions(0.98F, 0.98F)
                    .eyeHeight(0.15F)
                    .maxTrackingRange(10)
                    .trackingTickInterval(10)
    );

    public static void initialise() {
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
        final BigTntExplosionBehaviour behaviour = new BigTntExplosionBehaviour();
        behaviour.setKnockbackModifier(this.tntType.explosionRadius * 0.125f);
        SphereExplosion.explode(this.getWorld(), this.getBlockPos(), this.tntType.explosionRadius, behaviour);
    }

    protected void writeCustomData(WriteView view) {
        view.putInt("fuse", this.getFuse());
        view.putFloat("explosion_radius", this.tntType.explosionRadius);

        if (this.igniter != null) LazyEntityReference.writeData(this.igniter, view, "owner");
    }

    protected void readCustomData(ReadView view) {
        this.setFuse(view.getInt("fuse", 10 * 20));
        this.tntType.explosionRadius = view.getFloat("explosion_radius", 13f);
        this.igniter = LazyEntityReference.fromData(view, "igniter");
    }

    @Nullable
    public LivingEntity getOwner() {
        return LazyEntityReference.resolve(this.igniter, this.getWorld(), LivingEntity.class);
    }

    /*
    public void copyFrom(Entity original) {
        super.copyFrom(original);
        if (original instanceof BigTnt bigTnt) {
            this.causingEntity = bigTnt.causingEntity;
        }

    }
     */

    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }

    public BigTntType getTntType() {
        return this.tntType;
    }

    public final boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }
}

