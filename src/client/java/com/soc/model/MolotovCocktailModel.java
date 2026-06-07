package com.soc.model;

import net.minecraft.client.model.*;

public class MolotovCocktailModel extends HandGrenadeModel {
    public MolotovCocktailModel(ModelPart part) {
        super(part);
    }

    public static TexturedModelData getTexturedModelData() {
        final ModelData modelData = new ModelData();
        final ModelPartData modelPartData = modelData.getRoot();
        final ModelPartData sides = modelPartData.addChild("sides", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-2f, -9f, 2f, 4f, 9f, 1f, new Dilation(0f)), ModelTransform.origin(0f, 24f, 0f));

        final ModelPartData side_r1 = sides.addChild("side_r1", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-2f, -2f, 2f, 4f, 9f, 1f, new Dilation(0f)), ModelTransform.of(0f, -7f, 0f, 0f, 1.5708f, 0f));

        final ModelPartData side_r2 = sides.addChild("side_r2", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-2f, -2f, 2f, 4f, 9f, 1f, new Dilation(0f)), ModelTransform.of(0f, -7f, 0f, 0f, 3.1416f, 0f));

        final ModelPartData side_r3 = sides.addChild("side_r3", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-2f, -2f, 2f, 4f, 9f, 1f, new Dilation(0f)), ModelTransform.of(0f, -7f, 0f, 0f, -1.5708f, 0f));

        final ModelPartData neck = modelPartData.addChild("neck", ModelPartBuilder.create()
                .uv(0, 10).cuboid(-2f, 1f, -2f, 4f, 2f, 4f, new Dilation(0f))
                .uv(10, 3).cuboid(-1f, -3f, -1f, 2f, 4f, 2f, new Dilation(0f)), ModelTransform.origin(0f, 12f, 0f));

        final ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create()
                .uv(16, 5).cuboid(-2f, -8f, -2f, 4f, 7f, 4f, new Dilation(0f))
                .uv(16, 0).cuboid(-2f, -1f, -2f, 4f, 1f, 4f, new Dilation(0f)), ModelTransform.origin(0f, 24f, 0f));
        return TexturedModelData.of(modelData, 32, 16);
    }
}
