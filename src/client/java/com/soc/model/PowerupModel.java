package com.soc.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;

// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class PowerupModel extends EntityModel<EntityRenderState> {
	public PowerupModel(ModelPart part) {
		super(part);
	}
	public static TexturedModelData getTexturedModelData() {
		final ModelData modelData = new ModelData();
		final ModelPartData modelPartData = modelData.getRoot();
		final ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create()
				.uv(0, 10).cuboid(-3f, -8f, -1f, 6f, 2f, 2f, new Dilation(0f))
				.uv(-4, 7).cuboid(-4f, -6f, -1f, 8f, 2f, 2f, new Dilation(0f))
				.uv(3, 6).cuboid(-2f, -10f, -1f, 4f, 2f, 2f, new Dilation(0f))
				.uv(3, 5).cuboid(-1f, -12f, -1f, 2f, 2f, 2f, new Dilation(0f)), ModelTransform.origin(0f, 24f, 0f));
		return TexturedModelData.of(modelData, 16, 16);
	}
}