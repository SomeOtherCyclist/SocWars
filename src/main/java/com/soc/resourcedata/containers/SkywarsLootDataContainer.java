package com.soc.resourcedata.containers;

import com.google.gson.JsonObject;
import com.soc.SocWars;
import com.soc.lib.CumulativeWeightList;
import com.soc.resourcedata.deserialisation.PoolPopulation;
import com.soc.resourcedata.deserialisation.SkywarsItemData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.enchantment;
import static com.soc.lib.SocWarsLib.woolItemFromColour;
import static net.minecraft.util.JsonHelper.deserialize;

public class SkywarsLootDataContainer extends ItemDataContainer<SkywarsItemData> {
    private Map<Integer, CumulativeWeightList<Pair<Item, Integer>>[]> cumulativeWeightPools = new HashMap<>();
    private final Map<Integer, PoolPopulation> poolPopulations = new HashMap<>();

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

    public ItemStack getRandomItem(Integer poolKey, int tier, World world) throws IllegalStateException {
        final CumulativeWeightList<Pair<Item, Integer>>[] pool = this.cumulativeWeightPools.get(poolKey);
        if (pool == null) return ItemStack.EMPTY;

        try {
            final Pair<Item, Integer> weightedRandom = pool[tier].getRandom(world.random);
            if (weightedRandom == null) {
                return ItemStack.EMPTY;
            } else {
                final ItemStack stack = new ItemStack(weightedRandom.getLeft(), weightedRandom.getRight());
                if (stack.isIn(ItemTags.BOW_ENCHANTABLE)) stack.addEnchantment(enchantment(world, Enchantments.INFINITY), 1);
                return stack;
            }
        } catch (IndexOutOfBoundsException ignored) {
            return ItemStack.EMPTY;
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public void populateInventory(Inventory inventory, int tier, World world, int fillOrdinal, Optional<DyeColor> team) {
        if (fillOrdinal == 0) inventory.clear();

        final List<ItemStack> items = new ArrayList<>(inventory.size());
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) items.add(stack);
        }

        this.poolPopulations.forEach((pool, population) -> {
            final Integer count = population.getTier(tier).getRandom(world.random);
            if (count != null) for (int i = 0; i < count; i++) {
                if (fillOrdinal == 0 || world.random.nextDouble() < Math.pow(0.5d, fillOrdinal)) items.add(this.getRandomItem(pool, tier, world));
            }
        });

        while (items.size() < inventory.size()) items.add(ItemStack.EMPTY);

        Collections.shuffle(items);

        for (int i = 0; i < items.size() && i < inventory.size(); i++) {
            inventory.setStack(i, items.get(i));
        }

        team.ifPresent(woolColour -> {
            int slot = world.random.nextBetween(0, 26);
            inventory.setStack(slot, new ItemStack(woolItemFromColour(woolColour), 32));
        });
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
