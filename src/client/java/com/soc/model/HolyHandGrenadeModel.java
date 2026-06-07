package com.soc.model;

import com.soc.renderstate.HandGrenadeRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class HolyHandGrenadeModel extends EntityModel<HandGrenadeRenderState> {
	private final ModelPart pin;
	private final ModelPart mainbody;

	public HolyHandGrenadeModel(ModelPart root) {
		super(root);
		this.pin = root.getChild("pin");
		this.mainbody = root.getChild("mainbody");
	}

	public static TexturedModelData getTexturedModelData() {
		final ModelData modelData = new ModelData();
		final ModelPartData modelPartData = modelData.getRoot();
		final ModelPartData pin = modelPartData.addChild("pin", ModelPartBuilder.create()
				.uv(4, 4).cuboid(-4.0f, -24.0f, -1.0f, 2.0f, 12.0f, 2.0f, new Dilation(0.0f))
				.uv(16, 2).cuboid(-2.0f, -20.0f, -1.0f, 3.0f, 2.0f, 2.0f, new Dilation(0.0f))
				.uv(26, 2).cuboid(-7.0f, -20.0f, -1.0f, 3.0f, 2.0f, 2.0f, new Dilation(0.0f)), ModelTransform.origin(3.0f, 20.0f, 0.0f));

		final ModelPartData mainbody = modelPartData.addChild("mainbody", ModelPartBuilder.create()
				.uv(0, 6).cuboid(-9.0f, -10.0f, -6.0f, 12.0f, 12.0f, 12.0f, new Dilation(0.0f))
				.uv(28, 2).cuboid(3.0f, -8.0f, -4.0f, 2.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin(3.0f, 20.0f, 0.0f));

		final ModelPartData bottom_r1 = mainbody.addChild("bottom_r1", ModelPartBuilder.create()
				.uv(19, 8).cuboid(-3.0f, -1.0f, -5.0f, 8.0f, 2.0f, 8.0f, new Dilation(0.0f)), ModelTransform.of(-4.0f, 3.0f, -1.0f, 3.1416f, 0.0f, 0.0f));

		final ModelPartData top_r1 = mainbody.addChild("top_r1", ModelPartBuilder.create()
				.uv(4, 0).cuboid(3.0f, -7.0f, -5.0f, 2.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.of(0.0f, -7.0f, 1.0f, 0.0f, 0.0f, -1.5708f));

		final ModelPartData cube_r1 = mainbody.addChild("cube_r1", ModelPartBuilder.create()
				.uv(28, 2).cuboid(6.0f, -3.0f, -5.0f, 2.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.of(-2.0f, -5.0f, 0.0f, 0.0f, 1.5708f, 0.0f));

		final ModelPartData cube_r2 = mainbody.addChild("cube_r2", ModelPartBuilder.create()
				.uv(28, 2).cuboid(6.0f, -3.0f, -5.0f, 2.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.of(-3.0f, -5.0f, -1.0f, 0.0f, 3.1416f, 0.0f));

		final ModelPartData cube_r3 = mainbody.addChild("cube_r3", ModelPartBuilder.create()
				.uv(28, 2).cuboid(6.0f, -3.0f, -5.0f, 2.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.of(-4.0f, -5.0f, 0.0f, 0.0f, -1.5708f, 0.0f));
		return TexturedModelData.of(modelData, 51, 30);
	}

	public void removePin() {
		this.pin.hidden = true;
	}
}