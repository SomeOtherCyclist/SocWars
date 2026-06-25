package com.soc.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface OnBedUsed {
	boolean onUseBed(ServerPlayerEntity player, World world, BlockPos pos);
}
