package com.soc.mixin;

import com.soc.events.ModEvents;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
abstract class OnBedUsedTrigger {
	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	private void socwars_onBedUsed(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (player instanceof ServerPlayerEntity serverPlayer && !ModEvents.ON_BED_USED.invoker().onUseBed(serverPlayer, world, pos)) cir.setReturnValue(ActionResult.FAIL);
	}
}
