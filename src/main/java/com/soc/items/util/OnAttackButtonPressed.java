package com.soc.items.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface OnAttackButtonPressed {
	void onAttackButtonPressed(PlayerEntity player, ItemStack stack);
}
