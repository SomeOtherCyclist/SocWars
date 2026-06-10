package com.soc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.events.ModEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class OnPickUpItemEventTrigger implements Inventory {
	@Shadow public abstract boolean cannotPickup();

	@Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private void socwars_onPickUpItem(PlayerEntity player, CallbackInfo ci, @Local ItemStack itemStack) {
		if (!this.cannotPickup() && player instanceof ServerPlayerEntity serverPlayerEntity) ModEvents.ON_ITEM_PICKUP.invoker().onItemPickup(serverPlayerEntity, itemStack);
	}
}