package com.soc.renderer.entity;

import com.soc.SocWars;
import com.soc.entities.HandGrenadeEntity;
import com.soc.model.EntityModelLayers;
import com.soc.model.HandGrenadeModel;
import com.soc.renderstate.HandGrenadeRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HandGrenadeEntityRenderer<T extends HandGrenadeEntity> extends EntityRenderer<T, HandGrenadeRenderState> {
    private final HandGrenadeModel model;

    public HandGrenadeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new HandGrenadeModel(context.getPart(EntityModelLayers.HAND_GRENADE));
    }

    @Override
    public HandGrenadeRenderState createRenderState() {
        return new HandGrenadeRenderState();
    }

    protected Model getModel() {
        return this.model;
    }

    @Override
    public void render(HandGrenadeRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);

        matrices.push();
        this.setUpMatrices(matrices);

        this.getModel().render(matrices, vertexConsumers.getBuffer(this.getRenderLayer()), light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }

    protected void setUpMatrices(MatrixStack matrices) {
        matrices.translate(0d, 0.75d, 0d);
        matrices.scale(0.5f, -0.5f, -0.5f);
    }

    protected RenderLayer getRenderLayer() {
        return RenderLayer.getEntitySolid(Identifier.of(SocWars.MOD_ID, "textures/entity/hand_grenade.png"));
    }

    @Override
    protected void renderLabelIfPresent(HandGrenadeRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {}

    @Override
    public void updateRenderState(T entity, HandGrenadeRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.setDetonationTimer(entity.getDetonationTimer());
    }
}
