package com.soc.entities;

import com.soc.entities.util.ModEntities;
import com.soc.game.manager.GamesManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.soc.lib.SocWarsLib.randomCentredVec3d;

public class PowerupEntity extends Entity {
	public PowerupEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	public PowerupEntity(World world, BlockPos pos) {
		super(ModEntities.POWERUP, world);
		this.setPosition(pos.toCenterPos());
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void readCustomData(ReadView view) {}

	@Override
	protected void writeCustomData(WriteView view) {}

	@Override
	public void tick() {
		this.applyGravity();
		this.move(MovementType.SELF, this.getVelocity());
		this.tickBlockCollision();

		this.setVelocity(this.getVelocity().multiply(0.98d));

		super.tick();
	}

	@Override
	protected double getGravity() {
		return 0.04d;
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		final boolean wasPickedUp = GamesManager.getInstance().getGame(player).map(manager -> manager.onPowerupPickedUp((ServerPlayerEntity)player)).orElse(false);

		if (wasPickedUp) {
			this.discard();

			for (int i = 0; i < 20; i++) {
				final Vec3d random = randomCentredVec3d(this.random, 0.2d);
				this.getWorld().addParticleClient(ParticleTypes.TRIAL_SPAWNER_DETECTION, this.getX(), this.getY(), this.getZ(), random.x, random.y + 0.1d, random.z);
			}
			this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundCategory.PLAYERS,1f, 1f);
		}
	}
}
