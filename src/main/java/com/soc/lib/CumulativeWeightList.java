package com.soc.lib;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.soc.SocWars;
import com.soc.resourcedata.deserialisation.SkywarsItemData;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.util.JsonHelper.deserialize;

public class CumulativeWeightList<V> extends ArrayList<WeightedValue<V>> {
    private static final CumulativeWeightList<?> EMPTY = new CumulativeWeightList<>() {
        @Override
        public Object getRandom(Random random) {
            return null;
        }
    };

    @SuppressWarnings("unchecked")
	public static <V> CumulativeWeightList<V> empty() {
        return (CumulativeWeightList<V>)EMPTY;
    }

    public CumulativeWeightList() {}

    public CumulativeWeightList(WeightedValue<V>[] elements) {
        for (WeightedValue<V> element : elements) {
            if (element.weight() > 0f) {
                this.add(element.weight(), element.value());
            }
        }
    }

    public CumulativeWeightList(JsonObject json, Function<String, V> elementMapper) {
        json.asMap().forEach((key, element) -> {
            try {
                this.add(element.getAsFloat(), elementMapper.apply(key));
            } catch (UnsupportedOperationException uoe) {
                SocWars.LOGGER.warn("Failed to parse {} as a weight for a cumulative weight list from Json", element);
            } catch (Exception e) {
                SocWars.LOGGER.warn("Failed to transform Json element: {} into a value for a cumulative weight list", key);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public CumulativeWeightList(Map<?, SkywarsItemData> pool, int tier) {
        for (Map.Entry<?, SkywarsItemData> entry : pool.entrySet()) {
            final float weight = entry.getValue().getWeight(tier);
            if (weight > 0f) {
                this.add(weight, (V)Pair.of(entry.getKey(), entry.getValue().count()));
            }
        }
    }

    private float getTotalWeight() {
        return this.isEmpty() ? 0f : this.getLast().weight();
    }

    public V getRandom(Random random) {
        if (this.isEmpty()) return null;

        final float indexF = random.nextFloat() * this.getTotalWeight();
        final int index = Collections.binarySearch(this.stream().map(WeightedValue::weight).toList(), indexF);
        final int fixedIndex = index >= 0 ? index : -index - 1;
        return this.get(fixedIndex).value();
    }

    public void add(float weight, V value) {
        this.add(new WeightedValue<>(weight + this.getTotalWeight(), value));
    }
}
