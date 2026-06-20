package com.soc.renderer.entity;

import com.soc.SocWars;
import com.soc.entities.MolotovCocktailEntity;
import com.soc.model.EntityModelLayers;
import com.soc.model.MolotovCocktailModel;
import com.soc.renderstate.HandGrenadeRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MolotovCocktailEntityRenderer extends HandGrenadeEntityRenderer<MolotovCocktailEntity> {
    private final MolotovCocktailModel model;

    public MolotovCocktailEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new MolotovCocktailModel(context.getPart(EntityModelLayers.MOLOTOV_COCKTAIL));
    }

    protected Model getModel() {
        return this.model;
    }

    @Override
    public void render(HandGrenadeRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    protected void setUpMatrices(MatrixStack matrices) {
        matrices.translate(0d, 0.75d, 0d);
        matrices.scale(0.5f, -0.5f, -0.5f);
    }

    @Override
    protected RenderLayer getRenderLayer() {
        return RenderLayer.getEntityTranslucent(Identifier.of(SocWars.MOD_ID, "textures/entity/molotov_cocktail.png"));
    }
}
