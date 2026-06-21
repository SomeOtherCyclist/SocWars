package com.soc.mixin.client;

import com.soc.player.ClientPlayerDataManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
abstract class RenderPlayerIllusions extends LivingEntityRendererMixin {
	@Override
	protected <S extends PlayerEntityRenderState> void socwars_livingEntityRender(S livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		ClientPlayerDataManager.
	}
}
