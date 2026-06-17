package com.soc.gui.widget;

import net.minecraft.client.gui.screen.ButtonTextures;

import java.util.function.Consumer;

public class ToggleButtonWidget extends net.minecraft.client.gui.widget.ToggleButtonWidget {
    private final Consumer<Boolean> toggleFunction;

    public ToggleButtonWidget(int x, int y, int width, int height, boolean toggled, Consumer<Boolean> toggleFunction) {
        super(x, y, width, height, toggled);
        this.toggleFunction = toggleFunction;
    }

    public ToggleButtonWidget(int x, int y, int width, int height, boolean toggled, Consumer<Boolean> toggleFunction, ButtonTextures textures) {
        super(x, y, width, height, toggled);
        this.toggleFunction = toggleFunction;
        this.setTextures(textures);
    }

    public ToggleButtonWidget(int x, int y, int width, int height, boolean toggled, Consumer<Boolean> toggleFunction, ButtonTextures textures, boolean enabled) {
        this(x, y, width, height, toggled, toggleFunction, textures);
        this.active = enabled;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.setToggled(!this.toggled);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void setToggled(boolean toggled) {
        super.setToggled(toggled);
        this.toggleFunction.accept(this.toggled);
    }

    @Override
    public boolean isSelected() {
        return this.active && super.isSelected();
    }
}
