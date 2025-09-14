package com.soc.renderer;

import com.soc.blocks.blockentities.MapBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class MapBlockEntityRenderer implements BlockEntityRenderer<MapBlockEntity> {
    public MapBlockEntityRenderer(BlockEntityRendererFactory.Context context) {}


    @Override
    public void render(MapBlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        matrices.push();

        final VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        final Vec3d regionSize = new Vec3d(entity.getRegionSize());
        VertexRendering.drawBox(
                matrices,
                consumer,
                0d, 1d, 0d,
                regionSize.x, regionSize.y + 1d, regionSize.z,
                1f, 1f, 1f, 1f,
                0.13f, 0.13f, 0.13f);

        matrices.pop();
    }
}
