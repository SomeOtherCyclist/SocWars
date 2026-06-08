package com.soc.mixin;

import com.soc.events.ModEvents;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class AfterPlayerLeaveEventTrigger {
	@Inject(method = "remove", at = @At("RETURN"))
	private void afterPlayerLeave(ServerPlayerEntity player, CallbackInfo ci) {
		ModEvents.AFTER_PLAYER_LEAVE.invoker().afterLeave(player);
	}
}
