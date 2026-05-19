package com.soc.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
abstract class BlockOffsetEvaluateNullPos {
	@Inject(method = "getModelOffset", at = @At(value = "HEAD"), cancellable = true)
	private void socwars_blockOffsetEvaluateNullPos(BlockPos pos, CallbackInfoReturnable<Vec3d> cir) {
		if (pos == null) cir.setReturnValue(Vec3d.ZERO);
	}
}
