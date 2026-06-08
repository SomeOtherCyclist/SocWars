package com.soc.lib;

import com.soc.resourcedata.deserialisation.SkywarsItemData;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CumulativeWeightList<V> {
    private final List<Pair<Float, V>> weightList;

    public CumulativeWeightList() {
        this.weightList = new ArrayList<>();
    }

    public CumulativeWeightList(Pair<Float, V>[] elements) {
        this();
        for (Pair<Float, V> element : elements) {
            if (element.getLeft() > 0f) this.add(Pair.of(
                    element.getLeft() + this.getLastWeight(),
                    element.getRight()
            ));
        }
    }

    @SuppressWarnings("unchecked")
    public CumulativeWeightList(Map<?, SkywarsItemData> pool, int tier) {
        this();
        for (Map.Entry<?, SkywarsItemData> entry : pool.entrySet()) {
            final float weight = entry.getValue().getWeight(tier);
            if (weight > 0f) this.add((Pair<Float, V>) Pair.of(
                    weight + this.getLastWeight(),
                    Pair.of(entry.getKey(), entry.getValue().count())
            ));
        }
    }

    private float getLastWeight() {
        return this.weightList.isEmpty() ? 0f : this.weightList.getLast().getLeft();
    }

    public float getTotalWeight() {
        return this.weightList.getLast().getLeft();
    }

    public V getWeightedRandom(Random random) {
        if (this.weightList.isEmpty()) return null;

        final float indexF = random.nextFloat() * this.getTotalWeight();
        final int index = Collections.binarySearch(this.weightList.stream().map(Pair::getLeft).toList(), indexF);
        final int fixedIndex = index >= 0 ? index : -index - 1;
        return this.weightList.get(fixedIndex).getRight();
    }

    public void add(Pair<Float, V> element) {
        this.weightList.add(element);
    }
}
