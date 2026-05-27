package com.soc.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
abstract class GetBlockEntityEvaluateNullPos {
	@Inject(method = "getBlockEntity", at = @At("HEAD"), cancellable = true)
	private void socwars_getBlockEntityEvaluateNullPos(BlockPos pos, CallbackInfoReturnable<BlockEntity> cir) {
		if (pos == null) cir.setReturnValue(null);
	}
}
