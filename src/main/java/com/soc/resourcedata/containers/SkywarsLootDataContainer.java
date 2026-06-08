package com.soc.resourcedata.containers;

import com.google.gson.JsonObject;
import com.soc.SocWars;
import com.soc.lib.CumulativeWeightList;
import com.soc.resourcedata.deserialisation.PoolPopulation;
import com.soc.resourcedata.deserialisation.SkywarsItemData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.util.JsonHelper.deserialize;

public class SkywarsLootDataContainer extends ItemDataContainer<SkywarsItemData> {
    private Map<Integer, CumulativeWeightList<Pair<Item, Integer>>[]> cumulativeWeightPools = new HashMap<>();
    private Map<Integer, PoolPopulation> poolPopulations = new HashMap<>();

    private Map<Integer, CumulativeWeightList<Pair<Item, Integer>>[]> getCumulativeWeightPools() {
        return super.itemDataPools.keySet().stream().collect(Collectors.toMap(key -> key, this::getCumulativeWeightsForTiers));
    }

    @SuppressWarnings("unchecked")
    private CumulativeWeightList<Pair<Item, Integer>>[] getCumulativeWeightsForTiers(Integer poolKey) {
        final List<Integer> tiers = List.of(0, 1, 2, 3);
        return tiers.stream().map(tier -> this.getCumulativeWeightsForTier(poolKey, tier)).toArray(CumulativeWeightList[]::new);
    }

    private CumulativeWeightList<Pair<Item, Integer>> getCumulativeWeightsForTier(Integer poolKey, int tier) {
        if (tier < 0 || tier > 3) return null;
        return new CumulativeWeightList<>(super.itemDataPools.get(poolKey), tier);
    }

    public ItemStack getRandomItem(Integer poolKey, int tier, Random random) throws IllegalStateException {
        final CumulativeWeightList<Pair<Item, Integer>>[] pool = this.cumulativeWeightPools.get(poolKey);
        if (pool == null) return ItemStack.EMPTY;

        try {
            final Pair<Item, Integer> weightedRandom = pool[tier].getWeightedRandom(random);
            return weightedRandom == null ? ItemStack.EMPTY : new ItemStack(weightedRandom.getLeft(), weightedRandom.getRight());
        } catch (IndexOutOfBoundsException ignored) {
            return ItemStack.EMPTY;
        }
    }

    public List<ItemStack> getChestItems(int tier, Random random, int slots) {
        final List<ItemStack> items = new ArrayList<>(slots);
        this.poolPopulations.forEach((pool, population) -> {
            final int count = population.getTier(tier).get(random);
            for (int i = 0; i < count; i++) {
                items.add(this.getRandomItem(pool, tier, random));
            }
        });

        while (items.size() < slots) items.add(ItemStack.EMPTY);
        Collections.shuffle(items);

        return items;
    }

    public void readPoolPopulations(Reader reader) {
        final JsonObject json = deserialize(reader);
        json.asMap().forEach((poolString, element) -> {
            try {
                final int pool = Integer.parseInt(poolString);
                final PoolPopulation population = new PoolPopulation(element.getAsJsonObject());
                this.poolPopulations.put(pool, population);
            } catch(NumberFormatException nfe) {
                SocWars.LOGGER.warn("Failed to parse skywars pool population id: {}", poolString);
            } catch(IllegalStateException ise) {
                SocWars.LOGGER.warn("Failed to parse skywars pool population for pool id: {}", poolString);
            }
        });
    }

    @Override
    public void cache() {
        this.cumulativeWeightPools = this.getCumulativeWeightPools();
    }

    @Override
    public void clear() {
        super.clear();
        this.cumulativeWeightPools.clear();
        this.poolPopulations.clear();
    }
}
