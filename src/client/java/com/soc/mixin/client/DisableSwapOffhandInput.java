package com.soc.mixin.client;

import com.soc.SocWars;
import com.soc.items.components.ModComponents;
import com.soc.items.util.DisableOffhandSwapping;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
abstract class DisableSwapOffhandInput {
	@Shadow @Nullable public ClientPlayerEntity player;

	@Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", ordinal = 1))
	private boolean socwars_disableSwapOffhandInput(ClientPlayerEntity instance) {
		final ItemStack mainHandStack = instance.getStackInHand(Hand.MAIN_HAND);
		final ItemStack offHandStack = instance.getStackInHand(Hand.OFF_HAND);

		if (DisableOffhandSwapping.itemShouldDisable(mainHandStack) || DisableOffhandSwapping.itemShouldDisable(offHandStack)) {
			if (mainHandStack.get(ModComponents.DOUBLE_JUMP) != null) {
				emptyHand(instance, Hand.MAIN_HAND);
			}
			if (offHandStack.get(ModComponents.DOUBLE_JUMP) != null) {
				emptyHand(instance, Hand.OFF_HAND);
			}

			return true;
		}
		return instance.isSpectator();
	}

	@Unique
	private static void emptyHand(ClientPlayerEntity instance, Hand hand) {
		instance.setStackInHand(hand, ItemStack.EMPTY);
		throw new RuntimeException("you naughty bugger you really thought you could exploit this bug again"); //TODO: Remove this before uploading to avoid becoming the next landmaster
	}
}
