package com.soc.renderer;

import com.soc.blocks.blockentities.CollectibleBlockEntity;
import com.soc.blocks.util.ModBlocks;
import com.soc.player.ClientPlayerDataManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;

import static com.soc.lib.SocWarsLib.randomCentredVec3d;

public class CollectibleBlockEntityRenderer implements BlockEntityRenderer<CollectibleBlockEntity> {
    private final BlockRenderManager blockRenderManager;

    public CollectibleBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.blockRenderManager = context.getRenderManager();
    }

    @Override
    public void render(CollectibleBlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        matrices.push();

        matrices.translate(0.5f, 0f, 0.5f);
        matrices.multiply(new Quaternionf().rotateY(entity.getRotation()));
        matrices.translate(-0.5f, 0f, -0.5f);

        this.blockRenderManager.renderBlockAsEntity(ModBlocks.ITSEVOCAT_SKULL.getDefaultState(), matrices, vertexConsumers, ClientPlayerDataManager.hasCollectibleClient(entity.getId()) ? 2 : light, overlay);

        matrices.pop();
    }
}