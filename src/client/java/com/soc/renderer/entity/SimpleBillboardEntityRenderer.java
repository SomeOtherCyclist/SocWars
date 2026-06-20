package com.soc.renderer.entity;

import com.soc.lib.RenderHelper;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class SimpleBillboardEntityRenderer extends EntityRenderer<Entity, EntityRenderState> {
    private final RenderLayer layer;

    public SimpleBillboardEntityRenderer(EntityRendererFactory.Context context, Identifier texture) {
        super(context);
        this.shadowRadius = 0.5F;
        this.layer = RenderLayer.getEntityCutoutNoCull(texture);
    }

    public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrices.push();

        final VertexConsumer consumer = vertexConsumerProvider.getBuffer(this.layer);

        RenderHelper.renderTexturedQuad(matrices, consumer, this.dispatcher.getRotation(), light);
        super.render(state, matrices, vertexConsumerProvider, light);

        matrices.pop();
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void updateRenderState(Entity entity, EntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
    }

    @Override
    public boolean shouldRender(Entity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}
