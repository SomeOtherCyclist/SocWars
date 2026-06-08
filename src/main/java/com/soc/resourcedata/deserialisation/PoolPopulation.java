package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import com.soc.lib.CumulativeWeightList;

import static com.soc.lib.json.JsonHelper.getDefaultedObject;

public record PoolPopulation(CumulativeWeightList<Integer> tier0, CumulativeWeightList<Integer> tier1, CumulativeWeightList<Integer> tier2, CumulativeWeightList<Integer> tier3) {
	public static final String TIER_0_KEY = "tier_1";
	public static final String TIER_1_KEY = "tier_2";
	public static final String TIER_2_KEY = "tier_3";
	public static final String TIER_3_KEY = "tier_4";

	public PoolPopulation(JsonObject json) {
		this(
				getDefaultedObject(json, TIER_0_KEY, object -> new CumulativeWeightList<>(object, Integer::parseInt), CumulativeWeightList.empty()),
				getDefaultedObject(json, TIER_1_KEY, object -> new CumulativeWeightList<>(object, Integer::parseInt), CumulativeWeightList.empty()),
				getDefaultedObject(json, TIER_2_KEY, object -> new CumulativeWeightList<>(object, Integer::parseInt), CumulativeWeightList.empty()),
				getDefaultedObject(json, TIER_3_KEY, object -> new CumulativeWeightList<>(object, Integer::parseInt), CumulativeWeightList.empty())
		);
	}

	public CumulativeWeightList<Integer> getTier(int tier) {
		return switch (tier) {
			case 0 -> this.tier0;
			case 1 -> this.tier1;
			case 2 -> this.tier2;
			case 3 -> this.tier3;
			default -> CumulativeWeightList.empty();
		};
	}
}
