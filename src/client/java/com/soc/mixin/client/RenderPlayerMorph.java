package com.soc.mixin.client;

import com.soc.player.ClientPlayerDataManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.minecraft.client.render.entity.LivingEntityRenderer.getOverlay;
import static net.minecraft.util.math.MathHelper.sign;

@Mixin(PlayerEntityRenderer.class)
abstract class RenderPlayerMorph extends LivingEntityRendererBaseMixin {
	@Shadow public abstract Vec3d getPositionOffset(PlayerEntityRenderState playerEntityRenderState);

	@Unique
	private BlockRenderManager blockRenderManager;

	@Unique
	private BlockState morph;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void socwars_assignBlockRenderManager(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci){
		this.blockRenderManager = ctx.getBlockRenderManager();
	}

	@Override
	protected void socwars_livingEntityRender(LivingEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertices, int light, CallbackInfo ci) {
		final Entity thisEntity = Objects.requireNonNull(MinecraftClient.getInstance().world).getEntityById(((PlayerEntityRenderState)state).id);
		this.morph = thisEntity == null ? null : ClientPlayerDataManager.getMorph(thisEntity.getUuid());
		if (this.morph != null) {
			this.renderMorph(state, matrices, vertices, light, this.morph);
			ci.cancel();
		}
	}

	@Unique
	private void renderMorph(LivingEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertices, int light, BlockState morph) {
		matrices.push();

		if (((PlayerEntityRenderState)state).isInSneakingPose) {
			final double xOffset = state.x >= 0d ? -state.x % 1d : -state.x % 1d - 1d;
			final double yOffset = state.y >= -0.5d ? (-state.y - 0.5d) % 1d + 0.5d : (-state.y - 0.5d) % 1d - 0.5d;
			final double zOffset = state.z >= 0d ? -state.z % 1d : -state.z % 1d - 1d;
			matrices.translate(xOffset, yOffset, zOffset);
		} else {
			matrices.translate(-0.5d, 0d, -0.5d);
		}

		this.blockRenderManager.renderBlockAsEntity(morph, matrices, vertices, light, getOverlay(state, this.getAnimationCounter(state)));

		matrices.pop();
	}

	@Inject(method = "getPositionOffset(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
	private void socwars_noSneakOffsetWhenMorphed(PlayerEntityRenderState playerEntityRenderState, CallbackInfoReturnable<Vec3d> cir) {
		if (this.morph != null) cir.setReturnValue(Vec3d.ZERO);
	}
}
