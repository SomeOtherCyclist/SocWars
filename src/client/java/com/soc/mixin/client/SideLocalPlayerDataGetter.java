package com.soc.mixin.client;

import com.soc.player.ClientPlayerDataManager;
import com.soc.player.PlayerData;
import com.soc.player.PlayerDataManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerDataManager.class, remap = false)
abstract class SideLocalPlayerDataGetter {
	@Inject(method = "getSideLocalPlayerData", at = @At("HEAD"), cancellable = true)
	private static void socwars_getSideLocalPlayerData(PlayerEntity player, CallbackInfoReturnable<PlayerData> cir) {
		cir.setReturnValue(ClientPlayerDataManager.getPlayerData(player.getUuid()));
	}
}
