package com.soc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.items.util.CancelsBlockInteraction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
abstract class CancelItemInteraction {
	@Redirect(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;shouldCancelInteraction()Z"))
	private boolean socwars_shouldCancelInteraction(ServerPlayerEntity player, @Local(argsOnly = true) Hand hand) {
		if (player.getStackInHand(hand).getItem() instanceof CancelsBlockInteraction cancelsBlockInteraction) return cancelsBlockInteraction.shouldCancelInteraction();
		return player.shouldCancelInteraction();
	}
}
