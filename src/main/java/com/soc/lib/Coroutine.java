package com.soc.lib;

import java.util.function.Function;

public record Coroutine<T> (T context, Function<T, Boolean> function) {
    public boolean run() {
        return this.function.apply(this.context);
    }
}
