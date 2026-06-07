package com.soc.renderstate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.joml.Quaternionf;

public class PowerupRenderState extends EntityRenderState {
	private Quaternionf rotation = new Quaternionf();
	private final RenderTickCounter renderTickCounter;

	public PowerupRenderState() {
		super();
		this.renderTickCounter = MinecraftClient.getInstance().getRenderTickCounter();
	}

	public Quaternionf getRotation() {
		return this.rotation;
	}

	public void rotate() {
		this.rotation = this.rotation.rotateY(this.renderTickCounter.getFixedDeltaTicks() * 0.02f);
	}
}
