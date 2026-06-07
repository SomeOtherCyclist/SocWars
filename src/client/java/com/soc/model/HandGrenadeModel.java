package com.soc.model;

import com.soc.renderstate.HandGrenadeRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

public class HandGrenadeModel extends EntityModel<HandGrenadeRenderState> {
    public HandGrenadeModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        final ModelData modelData = new ModelData();
        final ModelPartData modelPartData = modelData.getRoot();
        final ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create()
                .uv(8, 15).cuboid(-7f, -12f, -1f, 8f, 12f, 8f, new Dilation(0f))
                .uv(0, 17).cuboid(-5f, -14f, 1f, 4f, 2f, 4f, new Dilation(0f))
                .uv(0, 11).cuboid(-5f, 0f, 1f, 4f, 2f, 4f, new Dilation(0f))
                .uv(20, 5).cuboid(-5f, -10f, -3f, 4f, 8f, 2f, new Dilation(0f))
                .uv(22, 0).cuboid(-7.5F, -18f, 1f, 6f, 1f, 4f, new Dilation(0f))
                .uv(4, 0).cuboid(-7.5F, -17f, 1.5F, 6f, 3f, 3f, new Dilation(0f)), ModelTransform.origin(3f, 22f, -3f));

        final ModelPartData handlelong_r1 = body.addChild("handlelong_r1", ModelPartBuilder.create()
                .uv(32, 5).cuboid(1.17f, -10.228f, -2f, 1f, 14f, 4f, new Dilation(0f)), ModelTransform.of(-12.5f, -9f, 3f, 0f, 0f, 0.3927f));

        final ModelPartData side_r1 = body.addChild("side_r1", ModelPartBuilder.create()
                .uv(20, 5).cuboid(-2f, -4f, -6f, 4f, 8f, 2f, new Dilation(0f)), ModelTransform.of(-3f, -6f, 3f, 0f, 1.5708f, 0f));

        final ModelPartData side_r2 = body.addChild("side_r2", ModelPartBuilder.create()
                .uv(20, 5).cuboid(-2f, -4f, -6f, 4f, 8f, 2f, new Dilation(0f)), ModelTransform.of(-3f, -6f, 3f, 0f, 3.1416f, 0f));

        final ModelPartData side_r3 = body.addChild("side_r3", ModelPartBuilder.create()
                .uv(20, 5).cuboid(-2f, -4f, -6f, 4f, 8f, 2f, new Dilation(0f)), ModelTransform.of(-3f, -6f, 3f, 0f, -1.5708f, 0f));

        final ModelPartData side_r4 = body.addChild("side_r4", ModelPartBuilder.create()
                .uv(20, 5).cuboid(-2f, -4f, -6f, 4f, 8f, 2f, new Dilation(0f)), ModelTransform.of(-3f, -6f, 3f, 0f, 0f, 0f));

        final ModelPartData pin = modelPartData.addChild("pin", ModelPartBuilder.create()
                .uv(12, 13).cuboid(-6f, -16f, 2f, 3f, 1f, 1f, new Dilation(0f))
                .uv(8, 7).cuboid(-4f, -17f, 2f, 1f, 1f, 1f, new Dilation(0f))
                .uv(8, 9).cuboid(-6f, -17f, 2f, 1f, 1f, 1f, new Dilation(0f))
                .uv(12, 11).cuboid(-6f, -18f, 2f, 3f, 1f, 1f, new Dilation(0f)), ModelTransform.origin(0f, 24f, 0f));
        return TexturedModelData.of(modelData, 42, 35);
    }
}
