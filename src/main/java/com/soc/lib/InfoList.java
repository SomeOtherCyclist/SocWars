package com.soc.lib;

import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class InfoList {
    private final ArrayList<Pair<Text, Text>> info;

    public InfoList() {
        this.info = new ArrayList<>();
    }

    public void add(Text display, Text hoverable) {
        info.add(Pair.of(display, hoverable));
    }

    public void add(BooleanSupplier predicate, Supplier<Text> display, Supplier<Text> hoverable) {
        if (predicate.getAsBoolean()) info.add(Pair.of(display.get(), hoverable.get()));
    }
    public void add(BooleanSupplier predicate, Text display, Text hoverable) {
        if (predicate.getAsBoolean()) info.add(Pair.of(display, hoverable));
    }

    public ArrayList<Pair<Text, Text>> getInfo() {
        return this.info;
    }

    public boolean isEmpty() {
        return info.isEmpty();
    }
}
