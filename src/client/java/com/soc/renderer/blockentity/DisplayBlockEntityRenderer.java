package com.soc.renderer.blockentity;

import com.soc.blocks.blockentities.DisplayBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class DisplayBlockEntityRenderer implements BlockEntityRenderer<DisplayBlockEntity> {
    private final ItemRenderer itemRenderer;

    public DisplayBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(DisplayBlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        matrices.push();
        matrices.translate(0.5d, 0.5d, 0.5d);

        final Direction direction = entity.getDirection();
        if (direction.getAxis() != Direction.Axis.Y) {
            matrices.multiply(new Quaternionf().rotateY(direction.getHorizontalQuarterTurns() * -1.5707964f));
        } else {
            matrices.multiply(new Quaternionf().rotateX(direction == Direction.UP ? -1.5707964f : 1.5707964f));
        }

        matrices.translate(0d, 0d, 0.465d);
        matrices.multiply(new Quaternionf().rotateZ(entity.getRotation() * 1.5707964f)); //Rotate it on the face

        this.itemRenderer.renderItem(entity.getDisplayItem(), ItemDisplayContext.FIXED, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);

        matrices.pop();
    }
}
