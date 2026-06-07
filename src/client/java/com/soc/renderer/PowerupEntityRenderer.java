package com.soc.renderer;

import com.soc.entities.PowerupEntity;
import com.soc.model.EntityModelLayers;
import com.soc.model.PowerupModel;
import com.soc.renderstate.PowerupRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static com.soc.renderer.HolyHandGrenadeEntityRenderer.BEAM_TEXTURE;

public class PowerupEntityRenderer extends EntityRenderer<PowerupEntity, PowerupRenderState> {
	private final PowerupModel model;

	protected PowerupEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.model = new PowerupModel(context.getPart(EntityModelLayers.POWERUP));
	}

	@Override
	public PowerupRenderState createRenderState() {
		return new PowerupRenderState();
	}

	@Override
	public void render(PowerupRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		super.render(state, matrices, vertexConsumers, light);

		matrices.push();
		matrices.multiply(state.getRotation());
		matrices.translate(0d, 2.125d, 0d);
		matrices.scale(1.5f, -1.5f, -1.5f);

		final RenderLayer renderLayer = RenderLayer.getEntitySolid(Identifier.ofVanilla("textures/block/gold_block.png"));
		this.model.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV);

		final float lastTickProgress = MinecraftClient.getInstance().gameRenderer.getCamera().getLastTickProgress();
		final long time = MinecraftClient.getInstance().world.getTime();

		matrices.pop();

		matrices.push();

		matrices.translate(-0.5d, 0.0d, -0.5d);
		BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, lastTickProgress, 1f, time, 1, 8, 0xffffffff, 0.1f, 0.15f);

		matrices.pop();

	}

	@Override
	public void updateRenderState(PowerupEntity entity, PowerupRenderState state, float tickProgress) {
		super.updateRenderState(entity, state, tickProgress);

		state.rotate();
	}

	@Override
	public boolean shouldRender(PowerupEntity entity, Frustum frustum, double x, double y, double z) {
		return true;
	}
}
