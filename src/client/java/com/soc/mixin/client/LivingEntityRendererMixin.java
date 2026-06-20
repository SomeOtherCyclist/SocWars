package com.soc.mixin.client;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
	@Inject(method = "rend")
}
