package com.soc.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;

public class HoverTexturedButtonWidget extends TexturedButtonWidget {
    final Text hoverText;
    final TextRenderer textRenderer;

    public HoverTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction, Text hoverText) {
        super(x, y, width, height, textures, pressAction);
        this.hoverText = hoverText;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    public HoverTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction, Text hoverText, boolean enabled) {
        this(x, y, width, height, textures, pressAction, hoverText);
        this.active = enabled;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
        if (this.hovered && this.active) {
            context.drawTooltip(this.textRenderer.wrapLines(this.hoverText, 120), mouseX, mouseY);
        }
    }

    @Override
    public boolean isSelected() {
        return this.active && super.isSelected();
    }
}
