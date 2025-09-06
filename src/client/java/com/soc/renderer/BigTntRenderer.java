package com.soc.renderer;

import com.soc.entities.BigTntEntity;
import com.soc.renderstate.BigTntRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class BigTntRenderer extends EntityRenderer<BigTntEntity, BigTntRenderState> {
    private final BlockRenderManager blockRenderManager;

    public BigTntRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    public void render(BigTntRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(-0.5f, 0f, -0.5f);

        TntMinecartEntityRenderer.renderFlashingBlock(
                this.blockRenderManager,
                state.getBlockState(),
                matrixStack,
                vertexConsumerProvider,
                i,
                state.fuse * state.fuse % 500f < 150f
        );

        matrixStack.pop();
        super.render(state, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public BigTntRenderState createRenderState() {
        return new BigTntRenderState(BigTntEntity.BigTntType.NUCLEAR);
    }

    @Override
    public void updateRenderState(BigTntEntity entity, BigTntRenderState state, float tickProgress) {
        state.setTntType(entity.getTntType());
        state.fuse = entity.getFuse() - tickProgress;

        super.updateRenderState(entity, state, tickProgress);
    }
}
