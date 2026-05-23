package com.soc.mixin.client;

import com.soc.items.util.OnAttackButtonPressed;
import com.soc.networking.c2s.OnAttackButtonPressedPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
abstract class OnAttackButtonPressedMixin {
	@Shadow @Nullable public ClientPlayerEntity player;

	@Inject(method = "doAttack", at = @At("HEAD"))
	private void socwars_onAttackButtonPress(CallbackInfoReturnable<Boolean> cir) {
		final ItemStack stack = this.player.getStackInHand(Hand.MAIN_HAND);
		if (stack.getItem() instanceof OnAttackButtonPressed onAttackButtonPressed) {
			onAttackButtonPressed.onAttackButtonPressed(this.player);
			ClientPlayNetworking.send(new OnAttackButtonPressedPayload(stack));
		}
	}
}
