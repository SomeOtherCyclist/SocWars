package com.soc.renderer;

import com.soc.blocks.blockentities.CollectibleBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class CollectibleBlockEntityRenderer implements BlockEntityRenderer<CollectibleBlockEntity> {
    private final ItemModelManager itemModelManager;

    public CollectibleBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemModelManager = context.getItemModelManager();
    }

    @Override
    public void render(CollectibleBlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        matrices.push();

        final RegistryEntry<Item> collectible = entity.getCollectible();

        if (collectible != null) {
            matrices.translate(0.5f, 0.5f, 0.5f);
            //matrices.scale(2f, 2f, 2f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) Math.PI));

            ItemRenderState state = new ItemRenderState();
            itemModelManager.updateForLivingEntity(state, collectible.value().getDefaultStack(), ItemDisplayContext.GROUND, MinecraftClient.getInstance().player);

            state.render(matrices, vertexConsumers, light, 0);
        }

        matrices.pop();
    }
}
