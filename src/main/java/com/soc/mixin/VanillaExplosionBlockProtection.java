package com.soc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.BiPredicate;

import static com.soc.game.manager.AbstractGameManager.getBlockDamagePredicate;

@Mixin(ExplosionImpl.class)
abstract class VanillaExplosionBlockProtection {
	@Shadow @Final private ServerWorld world;
	@Shadow @Final private DamageSource damageSource;

	@Unique private BiPredicate<BlockPos, BlockState> damagePredicate;

	@Inject(method = "getBlocksToDestroy", at = @At("HEAD"))
	private void socwars_vanillaExplosionBlockProtectionCachePredicate(CallbackInfoReturnable<List<BlockPos>> cir) {
		final Entity source = this.damageSource.getSource();
		this.damagePredicate = getBlockDamagePredicate(this.world, true, source instanceof Ownable ownableEntity ? ownableEntity.getOwner() : source);
	}

	@Redirect(method = "getBlocksToDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionBehavior;canDestroyBlock(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)Z"))
	private boolean socwars_vanillaExplosionBlockProtection(ExplosionBehavior instance, Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
		return instance.canDestroyBlock(explosion, world, pos, state, power) && isTest(pos, state);
	}

	@Unique
	private boolean isTest(BlockPos pos, BlockState state) {
		return this.damagePredicate.test(pos, state);
	}
}
