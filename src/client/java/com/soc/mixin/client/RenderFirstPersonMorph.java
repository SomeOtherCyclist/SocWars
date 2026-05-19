package com.soc.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.player.ClientPlayerDataManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class RenderFirstPersonMorph {
	@Redirect(method = "getEntitiesToRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;isThirdPerson()Z"))
	private boolean socwars_renderFirstPersonMorph(Camera camera, @Local Entity entity) {
		return ClientPlayerDataManager.getMorph(entity.getUuid()) != null || camera.isThirdPerson();
	}
}
