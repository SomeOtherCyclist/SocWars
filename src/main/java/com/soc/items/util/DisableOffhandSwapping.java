package com.soc.items.util;

import net.minecraft.item.ItemStack;

public interface DisableOffhandSwapping {
	boolean shouldDisable(ItemStack stack);

	static boolean itemShouldDisable(ItemStack stack) {
		return stack.getItem() instanceof DisableOffhandSwapping disableOffhandSwapping && disableOffhandSwapping.shouldDisable(stack);
	}
}
