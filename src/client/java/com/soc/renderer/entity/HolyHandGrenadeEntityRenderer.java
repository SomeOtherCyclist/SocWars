package com.soc.renderer.entity;

import com.soc.SocWars;
import com.soc.entities.HolyHandGrenadeEntity;
import com.soc.model.EntityModelLayers;
import com.soc.model.HolyHandGrenadeModel;
import com.soc.renderstate.HandGrenadeRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HolyHandGrenadeEntityRenderer extends HandGrenadeEntityRenderer<HolyHandGrenadeEntity> {
    public static final Identifier BEAM_TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/entity/holy_hand_grenade_beam.png");

    private final HolyHandGrenadeModel model;

    public HolyHandGrenadeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new HolyHandGrenadeModel(context.getPart(EntityModelLayers.HOLY_HAND_GRENADE));
    }

    protected Model getModel() {
        return this.model;
    }

    @Override
    public void render(HandGrenadeRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);

        if (state.getDetonationTimer() > 0f) {
            matrices.push();

            final float lastTickProgress = MinecraftClient.getInstance().gameRenderer.getCamera().getLastTickProgress();
            final long time = MinecraftClient.getInstance().world.getTime();

            matrices.translate(-0.5d, 0.0d, -0.5d);
            BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, lastTickProgress, 1f, time, 0, 2048, 0xffffffff, 0.15f, 0.2f);

            matrices.pop();
        }
    }

    @Override
    protected void setUpMatrices(MatrixStack matrices) {
        matrices.translate(0d, 0.75d, 0d);
        matrices.scale(0.5f, -0.5f, -0.5f);
    }

    @Override
    protected RenderLayer getRenderLayer() {
        return RenderLayer.getEntitySolid(Identifier.of(SocWars.MOD_ID, "textures/entity/holy_hand_grenade.png"));
    }
}
