package com.soc.renderer;

import com.soc.entities.PowerupEntity;
import com.soc.model.EntityModelLayers;
import com.soc.model.PowerupModel;
import com.soc.renderstate.PowerupRenderState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

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

		matrices.pop();
	}

	@Override
	public void updateRenderState(PowerupEntity entity, PowerupRenderState state, float tickProgress) {
		super.updateRenderState(entity, state, tickProgress);

		state.rotate();
	}
}
