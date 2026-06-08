package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;

import static com.soc.lib.json.JsonHelper.getDefaultedObject;

public record PoolPopulation(Range tier0, Range tier1, Range tier2, Range tier3) {
	public static final String TIER_0_KEY = "tier_1";
	public static final String TIER_1_KEY = "tier_2";
	public static final String TIER_2_KEY = "tier_3";
	public static final String TIER_3_KEY = "tier_4";

	public PoolPopulation(JsonObject json) {
		this(
				getDefaultedObject(json, TIER_0_KEY, Range::new, Range.EMPTY),
				getDefaultedObject(json, TIER_1_KEY, Range::new, Range.EMPTY),
				getDefaultedObject(json, TIER_2_KEY, Range::new, Range.EMPTY),
				getDefaultedObject(json, TIER_3_KEY, Range::new, Range.EMPTY)
		);
	}

	public Range getTier(int tier) {
		return switch (tier) {
			case 0 -> this.tier0;
			case 1 -> this.tier1;
			case 2 -> this.tier2;
			case 3 -> this.tier3;
			default -> Range.EMPTY;
		};
	}
}
