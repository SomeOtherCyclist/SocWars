package com.soc.events;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface OnItemDropped {
	boolean onDropItem(ServerPlayerEntity player, ItemStack stack);
}
