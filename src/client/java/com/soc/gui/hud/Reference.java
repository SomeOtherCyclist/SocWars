package com.soc.gui.hud;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.ifNotNull;

public class Reference<T> {
    public Reference() {
        this.value = null;
    }

    public Reference(@Nullable T value) {
        this.value = value;
    }

    private @Nullable T value;

    public void set(@Nullable T value) {
        this.value = value;
    }

    public @Nullable T get() {
        return this.value;
    }

    public void annul() {
        this.value = null;
    }

    public void ifPresent(Consumer<T> function) {
        ifNotNull(this.value, function);
    }

    public boolean isNull() {
        return this.value == null;
    }
}
