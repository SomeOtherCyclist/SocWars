package com.soc.gui.hud.sidebar;

import com.soc.gui.hud.VerticallyStackedHudComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class QueueHud implements VerticallyStackedHudComponent {
	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter renderTickCounter, TextRenderer textRenderer, int x, int y) {

	}
}
