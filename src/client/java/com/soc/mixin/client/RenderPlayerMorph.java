package com.soc.mixin.client;

import com.soc.player.ClientPlayerDataManager;
import com.soc.player.Morph;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.minecraft.client.render.entity.LivingEntityRenderer.getOverlay;

@Mixin(EntityRenderDispatcher.class)
abstract class RenderPlayerMorph {
	@Shadow public Camera camera;

	@Inject(method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At("HEAD"), cancellable = true)
	protected <S extends EntityRenderState> void socwars_livingEntityRender(S state, double x, double y, double z, MatrixStack matrices, VertexConsumerProvider vertices, int light, EntityRenderer<?, S> renderer, CallbackInfo ci) {
		if (state instanceof PlayerEntityRenderState playerState) {
			final ClientPlayerEntity player = MinecraftClient.getInstance().player;

			final Entity thisEntity = Objects.requireNonNull(MinecraftClient.getInstance().world).getEntityById(playerState.id);
			final Morph morph = thisEntity == null ? null : ClientPlayerDataManager.getMorph(thisEntity.getUuid());

			if (morph != null && !thisEntity.isSpectator()) {
				this.renderMorph(playerState, x, y, z, matrices, vertices, light, morph.blockState());

				if (!((player.isCreative() && (this.camera.isThirdPerson() || thisEntity != player)) || player.isSpectator())) ci.cancel();
			}
		}
	}

	@Unique
	private void renderMorph(PlayerEntityRenderState state, double x, double y, double z, MatrixStack matrices, VertexConsumerProvider vertices, int light, BlockState morph) {
		matrices.push();

		if (state.isInSneakingPose) {
			final Vec3d blockOffset = morph.getModelOffset(new BlockPos((int)state.x, (int)state.y, (int)state.z));

			final double xOffset = state.x >= 0d ? -state.x % 1d : -state.x % 1d - 1d;
			final double yOffset = state.y >= -0.5d ? (-state.y - 0.5d) % 1d + 0.5d : (-state.y - 0.5d) % 1d - 0.5d;
			final double zOffset = state.z >= 0d ? -state.z % 1d : -state.z % 1d - 1d;
			matrices.translate(x + xOffset + blockOffset.getX(), y + yOffset + blockOffset.getY(), z + zOffset + blockOffset.getZ());
		} else {
			matrices.translate(x - 0.5d, y, z - 0.5d);
		}

		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(morph, matrices, vertices, light, getOverlay(state, 0f));

		matrices.pop();
	}
}
