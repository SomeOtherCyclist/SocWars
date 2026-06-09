package com.soc.util;

import com.soc.SocWars;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface ItemTags {

	
	private static TagKey<Item> fromString(String id) {
		return TagKey.of(RegistryKeys.ITEM, Identifier.of(SocWars.MOD_ID, id));
	}

	static void initialise() {}
}
