package com.soc.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class NumberTextFieldWidget extends TextFieldWidget {
    private final Consumer<Integer> charTypedCallback;
    private final int maxValue;
    private String text;

    public NumberTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, int maxValue, Consumer<Integer> charTypedCallback) {
        super(textRenderer, x, y, width, height, text);
        this.charTypedCallback = charTypedCallback;
        this.maxValue = maxValue;

        this.setMaxLength(4);
        this.setChangedListener(string -> this.charTypedCallback.accept(this.parseInt(string)));
    }

    public int parseInt(String text) {
        int value = text.isEmpty() ? 1 : Integer.parseInt(text);
        return Math.min(value, maxValue);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!Character.isDigit(chr)) return false;

        return super.charTyped(chr, modifiers);
    }
}