package com.soc.renderer.entity;

import com.soc.SocWars;
import com.soc.entities.JetShoppingTrolleyEntity;
import com.soc.model.EntityModelLayers;
import com.soc.model.JetShoppingTrolleyModel;
import com.soc.renderstate.JetShoppingTrolleyEntityRenderState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class JetShoppingTrolleyEntityRenderer extends EntityRenderer<JetShoppingTrolleyEntity, JetShoppingTrolleyEntityRenderState> {
    public static final RenderLayer RENDER_LAYER = RenderLayer.getEntityCutout(Identifier.of(SocWars.MOD_ID, "textures/entity/jet_shopping_trolley.png"));

    private final JetShoppingTrolleyModel model;

    public JetShoppingTrolleyEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new JetShoppingTrolleyModel(context.getPart(EntityModelLayers.JET_SHOPPING_TROLLEY));
    }

    @Override
    public void render(JetShoppingTrolleyEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);
        matrices.push();

        matrices.multiply(state.getRotation());

        matrices.translate(0f, 2.5f, -0.1f);
        matrices.scale(-1.6f, -1.6f, 1.6f);
        this.model.render(matrices, vertexConsumers.getBuffer(RENDER_LAYER), light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }

    @Override
    public JetShoppingTrolleyEntityRenderState createRenderState() {
        return new JetShoppingTrolleyEntityRenderState();
    }

    @Override
    public void updateRenderState(JetShoppingTrolleyEntity entity, JetShoppingTrolleyEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.setRotation(entity.getYaw());
    }
}
