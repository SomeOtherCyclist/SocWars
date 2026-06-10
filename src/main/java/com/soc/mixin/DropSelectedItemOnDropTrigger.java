package com.soc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.events.ModEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
abstract class DropSelectedItemOnDropTrigger {
	@Shadow @Final public PlayerEntity player;

	@Inject(method = "dropSelectedItem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerInventory;getSelectedStack()Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
	private void dropSelectedItemOnDropTrigger(boolean entireStack, CallbackInfoReturnable<ItemStack> cir, @Local ItemStack stack) {
		if (stack != null && this.player instanceof ServerPlayerEntity serverPlayer && !ModEvents.ON_ITEM_DROPPED.invoker().onDropItem(serverPlayer, stack)) {
			serverPlayer.onSpawn(); //yeh this bad
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}
}
