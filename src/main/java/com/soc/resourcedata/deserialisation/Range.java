package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import net.minecraft.util.math.random.Random;

import static com.soc.lib.json.JsonHelper.getDefaultedInt;

public record Range(int min, int max) {
	public static String MIN_KEY = "min";
	public static String MAX_KEY = "max";

	public static Range EMPTY = new Range(0, 0);

	public Range(JsonObject json) {
		this(getDefaultedInt(json, MIN_KEY), getDefaultedInt(json, MAX_KEY));
	}

	public int get(Random random) {
		return random.nextBetween(this.min, this.max);
	}
}
