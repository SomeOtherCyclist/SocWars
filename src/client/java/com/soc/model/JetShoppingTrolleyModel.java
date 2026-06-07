package com.soc.model;

import com.soc.renderstate.JetShoppingTrolleyEntityRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class JetShoppingTrolleyModel extends EntityModel<JetShoppingTrolleyEntityRenderState> {
	private final ModelPart wheels;

	public JetShoppingTrolleyModel(ModelPart root) {
        super(root);
        this.wheels = root.getChild("wheels");
	}
	public static TexturedModelData getTexturedModelData() {
		final ModelData modelData = new ModelData();
		final ModelPartData modelPartData = modelData.getRoot();
		final ModelPartData cage = modelPartData.addChild("cage", ModelPartBuilder.create()
				.uv(0, -12).cuboid(4f, -3f, -12f, 0f, 6f, 12f, new Dilation(0f)), ModelTransform.origin(0f, 16f, 8f));

		final ModelPartData bottom_r1 = cage.addChild("bottom_r1", ModelPartBuilder.create()
				.uv(0, 1).cuboid(-6f, -4f, 0f, 12f, 8f, 0f, new Dilation(0f)), ModelTransform.of(0f, 3f, -6f, -1.5708f, 1.5708f, 0f));

		final ModelPartData front_r1 = cage.addChild("front_r1", ModelPartBuilder.create()
				.uv(4, 4).cuboid(-4f, -3f, 0f, 8f, 6f, 0f, new Dilation(0f)), ModelTransform.of(0f, 0f, 0f, 0f, 3.1416f, -3.1416f));

		final ModelPartData left_r1 = cage.addChild("left_r1", ModelPartBuilder.create()
				.uv(0, -12).cuboid(0f, -3f, -6f, 0f, 6f, 12f, new Dilation(0f)), ModelTransform.of(-4f, 0f, -6f, 0f, 3.1416f, 0f));

		final ModelPartData back_r1 = cage.addChild("back_r1", ModelPartBuilder.create()
				.uv(4, 4).cuboid(-4f, -3f, 0f, 8f, 6f, 0f, new Dilation(0f)), ModelTransform.of(0f, 0f, -12f, 0f, 0f, -3.1416f));

		final ModelPartData boosters = modelPartData.addChild("boosters", ModelPartBuilder.create()
				.uv(13, 0).cuboid(-13f, -1f, -6.5F, 3f, 3f, 11f, new Dilation(0f))
				.uv(10, 11).cuboid(-13f, -1f, -5.5F, 3f, 3f, 0f, new Dilation(0f))
				.uv(10, 11).cuboid(-2f, -1f, -5.5F, 3f, 3f, 0f, new Dilation(0f)), ModelTransform.origin(6f, 16f, -1.5F));

		final ModelPartData right_r1 = boosters.addChild("right_r1", ModelPartBuilder.create()
				.uv(13, 0).cuboid(-1.5F, -1.5F, -5.5F, 3f, 3f, 11f, new Dilation(0f)), ModelTransform.of(-0.5F, 0.5F, -1f, 0f, 0f, -3.1416f));

		final ModelPartData wheels = modelPartData.addChild("wheels", ModelPartBuilder.create()
				.uv(4, 10).cuboid(5f, -2f, -1f, 1f, 2f, 2f, new Dilation(0f))
				.uv(4, 10).cuboid(-2f, -2f, -11f, 1f, 2f, 2f, new Dilation(0f))
				.uv(4, 10).cuboid(5f, -2f, -11f, 1f, 2f, 2f, new Dilation(0f))
				.uv(4, 10).cuboid(-2f, -2f, -1f, 1f, 2f, 2f, new Dilation(0f)), ModelTransform.origin(-2f, 24f, 6f));

		final ModelPartData frame = modelPartData.addChild("frame", ModelPartBuilder.create()
				.uv(0, 14).cuboid(-4f, -3f, 6f, 8f, 1f, 1f, new Dilation(0f)), ModelTransform.origin(0f, 24f, 0f));

		final ModelPartData upright_r1 = frame.addChild("upright_r1", ModelPartBuilder.create()
				.uv(20, 16).cuboid(-5f, -0.5F, -0.5F, 9f, 1f, 1f, new Dilation(0f)), ModelTransform.of(3.5F, -7f, -4.5F, 0f, 0f, 1.5708f));

		final ModelPartData upleft_r1 = frame.addChild("upleft_r1", ModelPartBuilder.create()
				.uv(0, 16).cuboid(-5f, -0.5F, -0.5F, 9f, 1f, 1f, new Dilation(0f)), ModelTransform.of(-3.5F, -7f, -4.5F, 0f, 0f, 1.5708f));

		final ModelPartData bottomleft_r1 = frame.addChild("bottomleft_r1", ModelPartBuilder.create()
				.uv(18, 14).cuboid(-5.5F, -0.5F, -0.5F, 11f, 1f, 1f, new Dilation(0f)), ModelTransform.of(-3.5F, -2.5F, 0.5F, 0f, -1.5708f, 0f));

		final ModelPartData bottomright_r1 = frame.addChild("bottomright_r1", ModelPartBuilder.create()
				.uv(18, 14).cuboid(-5.5F, -0.5F, -0.5F, 11f, 1f, 1f, new Dilation(0f)), ModelTransform.of(3.5F, -2.5F, 0.5F, 0f, -1.5708f, 0f));

		final ModelPartData handle = modelPartData.addChild("handle", ModelPartBuilder.create()
				.uv(0, 18).cuboid(-4f, -0.5F, -0.5F, 8f, 1f, 1f, new Dilation(0f)), ModelTransform.origin(0f, 12.5F, -7.5F));

		final ModelPartData right_r2 = handle.addChild("right_r2", ModelPartBuilder.create()
				.uv(24, 18).cuboid(-1f, -1f, -1f, 2f, 1f, 1f, new Dilation(0f)), ModelTransform.of(3f, 0.5F, 1.5F, 0f, -1.5708f, 0f));

		final ModelPartData left_r2 = handle.addChild("left_r2", ModelPartBuilder.create()
				.uv(18, 18).cuboid(-1f, -1f, -1f, 2f, 1f, 1f, new Dilation(0f)), ModelTransform.of(-4f, 0.5F, 1.5F, 0f, -1.5708f, 0f));
		return TexturedModelData.of(modelData, 42, 20);
	}
}