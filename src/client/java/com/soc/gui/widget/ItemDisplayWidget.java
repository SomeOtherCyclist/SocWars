package com.soc.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class ItemDisplayWidget implements Widget, Selectable, Drawable, Element {
    private int x;
    private int y;
    private final int size;
    private final ItemStack stack;

    private boolean isFocused;
    private boolean isHovered;
    private boolean isVisible = true;

    private final TextRenderer textRenderer;

    public ItemDisplayWidget(int x, int y, int size, ItemStack stack) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.stack = stack;

        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.isVisible) {
            context.drawItem(this.stack, this.x, this.y);
            if (this.stack.getCount() > 1) {
                final String count = String.valueOf(this.stack.getCount());
                context.drawText(this.textRenderer, count, this.x + 17 - this.textRenderer.getWidth(count), this.y + 9, 0xffffffff, true);
            }
            this.isHovered = context.scissorContains(mouseX, mouseY) && this.mouseIsInBounds(mouseX, mouseY);
            if (this.isHovered) context.drawTooltip(this.textRenderer, Screen.getTooltipFromItem(MinecraftClient.getInstance(), this.stack), this.stack.getTooltipData(), mouseX, mouseY, this.stack.get(DataComponentTypes.TOOLTIP_STYLE));
        }
    }

    private boolean mouseIsInBounds(int mouseX, int mouseY) {
        return
                mouseX >= this.x &&
                mouseY >= this.y &&
                mouseX < this.x + this.size &&
                mouseY < this.y + this.size;
    }

    @Override
    public void setFocused(boolean isFocused) {
        this.isFocused = isFocused;
    }

    @Override
    public boolean isFocused() {
        return this.isFocused;
    }

    @Override
    public SelectionType getType() {
        return this.isHovered ? SelectionType.HOVERED : this.isFocused ? SelectionType.FOCUSED : SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.stack.getName());
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.size;
    }

    @Override
    public int getHeight() {
        return this.size;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return Widget.super.getNavigationFocus();
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {}
}
