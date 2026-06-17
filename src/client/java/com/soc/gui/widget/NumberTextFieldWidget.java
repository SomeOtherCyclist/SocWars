package com.soc.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class NumberTextFieldWidget extends TextFieldWidget {
    private final Consumer<Integer> charTypedCallback;
    private final int minValue;
    private final int maxValue;

    public NumberTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, int minValue, int maxValue, Consumer<Integer> charTypedCallback) {
        super(textRenderer, x, y, width, height, text);
        this.charTypedCallback = charTypedCallback;
        this.minValue = minValue;
        this.maxValue = maxValue;

        this.setMaxLength(4);
        this.setChangedListener(string -> this.charTypedCallback.accept(this.parseInt(string)));
    }

    public NumberTextFieldWidget(TextRenderer textRenderer, int width, int height, Text text, int minValue, int maxValue, Consumer<Integer> charTypedCallback) {
        this(textRenderer, 0, 0, width, height, text, minValue, maxValue, charTypedCallback);
    }

    public NumberTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, int maxValue, Consumer<Integer> charTypedCallback) {
        this(textRenderer, x, y, width, height, text, 0, maxValue, charTypedCallback);
    }

    public NumberTextFieldWidget(TextRenderer textRenderer, int width, int height, Text text, int maxValue, Consumer<Integer> charTypedCallback) {
        this(textRenderer, 0, 0, width, height, text, maxValue, charTypedCallback);
    }

    public int parseInt(String text) {
        int value = text.isEmpty() || text.equals("-") ? 1 : Integer.parseInt(text);
        if (value < this.minValue || value > this.maxValue) {
            value = Math.clamp(value, this.minValue, this.maxValue);
            this.setText(String.valueOf(value));
        }
        return value;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!(Character.isDigit(chr) || (this.minValue < 0 && chr == '-'))) return false;

        return super.charTyped(chr, modifiers);
    }
}