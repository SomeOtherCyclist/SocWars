package com.soc.renderer.blockentity;

import com.soc.blocks.blockentities.KitBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.List;

public class KitBlockEntityRenderer implements BlockEntityRenderer<KitBlockEntity> {
    private final TextRenderer textRenderer;
    private final ItemRenderer itemRenderer;
    private final BlockEntityRenderDispatcher renderDispatcher;

    public KitBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.textRenderer = context.getTextRenderer();
        this.itemRenderer = context.getItemRenderer();
        this.renderDispatcher = context.getRenderDispatcher();
    }

    @Override
    public void render(KitBlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        if (entity.hasValidKit()) {
            this.renderLabel(entity, matrices, vertexConsumers);
            this.renderItems(entity, matrices, vertexConsumers, light);
        }
    }

    private void renderLabel(KitBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();

        matrices.translate(0.5d, 1.5d, 0.5d);
        matrices.multiply(this.renderDispatcher.camera.getRotation());
        matrices.scale(0.025f, -0.025f, 0.025f);

        final Text name = entity.getKit().getTextName();
        final float xOffset = this.textRenderer.getWidth(name) * -0.5f;

        final int backgroundColour = (int)(MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f) * 255f) << 24;

        //I have no idea how or why this light value works but screw it we ball
        this.textRenderer.draw(name, xOffset, 0f, 0xffffffff, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.POLYGON_OFFSET, backgroundColour, 5243056);

        matrices.pop();
    }

    private void renderItems(KitBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.translate(0.5d, 0.175d, 0.5d);

        //TODO: get rid of all of the magic numbers and clean it up because this is gross
        final float time = (float) (MinecraftClient.getInstance().world.getTime() % 1000) + MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
        final Quaternionf rotation = new Quaternionf().rotateY(time * (float) Math.PI * 0.02f);

        final List<ItemStack> heldStacks = entity.getKit().getHeldStacks();

        for (int i = 0; i < heldStacks.size(); i++) {
            matrices.push();

            final int index = i * 7;

            //yes shut up I know, that division is intentional I want it to floor
            final double yaw = (index + (i / 5) * 0.5f) * 0.2d * Math.PI;

            final double x = Math.sin(yaw) * 0.25d;
            final double y = (index % 2) * 0.25d;
            final double z = Math.cos(yaw) * 0.25d;

            matrices.translate(x, y, z);
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.multiply(rotation);
            rotation.rotateY(index * 0.07f);

            this.itemRenderer.renderItem(heldStacks.get(i), ItemDisplayContext.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), i);

            matrices.pop();
        }

        matrices.pop();
    }

    private void shouldRender() {

    }
}
