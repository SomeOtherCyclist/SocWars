package com.soc.entities;

import com.soc.SocWars;
import com.soc.util.SphereExplosion;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
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

public class BigTnt extends Entity implements Ownable {
    private static final TrackedData<Integer> FUSE = DataTracker.registerData(BigTnt.class, TrackedDataHandlerRegistry.INTEGER);
    private LazyEntityReference<LivingEntity> igniter;
    private float explosionRadius;

    public BigTnt(EntityType<Entity> entityType, World world) {
        super(entityType, world);
        this.explosionRadius = 13.0F;
        this.intersectionChecked = true;
    }

    public BigTnt(World world, Vec3d position, @Nullable LivingEntity igniter, float explosionRadius) {
        this(NUCLEAR_BOMB, world);
        this.setPosition(position);
        this.setVelocity(new Vec3d(0, 0, 0));
        this.setFuse(10 * 20);
        this.igniter = new LazyEntityReference<>(igniter);
        this.explosionRadius = explosionRadius;
    }

    public static final EntityType<Entity> NUCLEAR_BOMB = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(SocWars.MOD_ID, "nuclear_bomb"),
            EntityType.Builder.create(BigTnt::new, SpawnGroup.MISC).dropsNothing().makeFireImmune().dimensions(0.98F, 0.98F).eyeHeight(0.15F).maxTrackingRange(10).trackingTickInterval(10).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(SocWars.MOD_ID, "nuclear_bomb")))
    );

    public static void initialise() {
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(FUSE, 10 * 20);
    }

    /*
    protected Entity.MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    public boolean canHit() {
        return !this.isRemoved();
    }
    */

    protected double getGravity() {
        return 0.04;
    }

    public void tick() {
        this.applyGravity();
        this.tickBlockCollision();
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.8));

        if (getFuse() > 0) {
            setFuse(getFuse() - 1);
        } else {
            this.discard();
            SphereExplosion.explode(this.getWorld(), this.getBlockPos(), this.explosionRadius);
        }
    }

    private void explode() {
        World var2 = this.getWorld();
        if (var2 instanceof ServerWorld serverWorld) {
            if (serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
                this.getWorld().createExplosion(this, Explosion.createDamageSource(this.getWorld(), this), null, this.getX(), this.getBodyY((double)0.0625F), this.getZ(), this.explosionRadius, false, World.ExplosionSourceType.TNT);
            }
        }

    }

    protected void writeCustomData(WriteView view) {
        view.putInt("fuse", this.getFuse());
        view.putFloat("explosion_radius", this.explosionRadius);

        LazyEntityReference.writeData(this.igniter, view, "owner");
    }

    protected void readCustomData(ReadView view) {
        this.setFuse(view.getInt("fuse", 10 * 20));
        this.explosionRadius = view.getFloat("explosion_radius", 13f);
        this.igniter = LazyEntityReference.fromData(view, "igniter");
    }

    @Nullable
    public LivingEntity getOwner() {
        return (LivingEntity)LazyEntityReference.resolve(this.igniter, this.getWorld(), LivingEntity.class);
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

    public final boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }
}

