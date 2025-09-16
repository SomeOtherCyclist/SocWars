package com.soc.lib;

import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class InfoList {
    public enum InfoType {
        ERROR,
        WARNING,
        INFO
    }

    private final ArrayList<Pair<Text, Text>> info;
    private final HashMap<InfoType, AtomicInteger> accumulators;

    public InfoList() {
        this.info = new ArrayList<>();

        {
            InfoType[] infoTypes = InfoType.values();
            this.accumulators = new HashMap<>(infoTypes.length);
            Arrays.stream(infoTypes).forEach(infoType -> this.accumulators.put(infoType, new AtomicInteger(0)));
        }
    }

    private void accumulate(@Nullable InfoType infoType) {
        if (infoType == null) return;
        this.accumulators.get(infoType).getAndIncrement();
    }

    public void addEmpty() {
        this.info.add(Pair.of(Text.empty(), Text.empty()));
    }
    public void addEmpty(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) this.info.add(Pair.of(Text.empty(), Text.empty()));
    }

    public void add(Text display, Text hoverable, @Nullable InfoType infoType) {
        this.accumulate(infoType);
        this.info.add(Pair.of(display, hoverable));
    }
    public void add(BooleanSupplier predicate, Text display, Text hoverable, @Nullable InfoType infoType) {
        this.accumulate(infoType);
        if (predicate.getAsBoolean()) this.info.add(Pair.of(display, hoverable));
    }
    public void add(BooleanSupplier predicate, Supplier<Text> display, Supplier<Text> hoverable, @Nullable InfoType infoType) {
        this.accumulate(infoType);
        if (predicate.getAsBoolean()) this.info.add(Pair.of(display.get(), hoverable.get()));
    }

    public ArrayList<Pair<Text, Text>> getInfo() {
        return this.info;
    }

    public HashMap<InfoType, AtomicInteger> getAccumulators() {
        return this.accumulators;
    }
    public int getAccumulator(InfoType infoType) {
        return this.accumulators.get(infoType).get();
    }

    public boolean hasErrors() {
        return this.getAccumulator(InfoType.ERROR) > 0;
    }
    public boolean hasWarnings() {
        return this.getAccumulator(InfoType.WARNING) > 0;
    }

    public boolean isEmpty() {
        return this.info.isEmpty();
    }

    public InfoList concat(InfoList other) {
        this.info.addAll(other.info);
        this.accumulators.keySet().forEach(infoType -> this.accumulators.get(infoType).addAndGet(other.getAccumulator(infoType)));

        return this;
    }
}
