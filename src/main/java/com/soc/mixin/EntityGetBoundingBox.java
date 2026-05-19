package com.soc.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
abstract class EntityGetBoundingBox {
	@Shadow public abstract World getWorld();

	@Shadow public abstract BlockPos getBlockPos();

	@Shadow public abstract Vec3d getPos();

	@Shadow public abstract boolean isSneaking();

	@Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true)
	protected void socwars_getBoundingBox(CallbackInfoReturnable<Box> cir) {}
}
