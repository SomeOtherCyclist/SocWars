package com.soc.renderer.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class SimpleCubeEntityRenderer extends EntityRenderer<Entity, EntityRenderState> {
    private final BlockState block;
    private final float scale;

    public SimpleCubeEntityRenderer(EntityRendererFactory.Context context, BlockState block, float scale) {
        super(context);
        this.block = block;
        this.scale = scale;
        this.shadowRadius = 0.5F;
    }

    public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrices.push();

        matrices.translate(-this.scale * 0.5f, 0f, -this.scale * 0.5f);
        matrices.scale(this.scale, this.scale, this.scale);

        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(this.block, matrices, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV);

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
