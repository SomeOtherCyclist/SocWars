package com.soc.gui.widget;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class DisableableButtonWidget extends ButtonWidget {
	public DisableableButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, boolean enabled) {
		super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
		this.active = enabled;
	}

	@Override
	public boolean isSelected() {
		return this.active && super.isSelected();
	}
}
