package com.soc.renderer.entity;

import com.soc.SocWars;
import com.soc.entities.BedwarsShopEntity;
import com.soc.game.manager.bedwars.ShopType;
import com.soc.model.BedwarsShopEntityModel;
import com.soc.renderstate.BedwarsShopRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class BedwarsShopEntityRenderer extends LivingEntityRenderer<BedwarsShopEntity, BedwarsShopRenderState, BedwarsShopEntityModel> {
    private final Identifier skinTexture;

    public BedwarsShopEntityRenderer(EntityRendererFactory.Context context, ShopType shopType) {
        super(context, new BedwarsShopEntityModel(MinecraftClient.getInstance().getLoadedEntityModels().getModelPart(EntityModelLayers.PLAYER_SLIM)), 0.5f);
        //this.skinTexture = Identifier.of(SocWars.MOD_ID, Math.random() < 0.95d ? "textures/shop_skins/bat_pink.png" : "textures/shop_skins/bat_green.png");
        this.skinTexture = switch (shopType) {
            case INDIVIDUAL -> Identifier.of(SocWars.MOD_ID, "textures/shop_skins/bat_pink.png");
            case TEAM -> Identifier.of(SocWars.MOD_ID, "textures/shop_skins/bat_green.png");
        };
    }

    public void render(BedwarsShopRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(new Quaternionf().rotateY(state.getRotationXZ()));

        super.render(state, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    @Override
    protected void renderLabelIfPresent(BedwarsShopRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {}

    @Override
    public Identifier getTexture(BedwarsShopRenderState state) {
        return this.skinTexture;
    }

    @Override
    public BedwarsShopRenderState createRenderState() {
        return new BedwarsShopRenderState();
    }

    @Override
    public void updateRenderState(BedwarsShopEntity entity, BedwarsShopRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.setRotationXZ(getPlayerDirection(state, tickProgress));
    }

    private static float getPlayerDirection(BedwarsShopRenderState state, float tickProgress) {
        return 4.71238898038469f-(float)(Math.atan2(state.z - MinecraftClient.getInstance().player.getLerpedPos(tickProgress).z, state.x - MinecraftClient.getInstance().player.getLerpedPos(tickProgress).x));
    }
}
