package com.soc.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public interface HoldsBlockEntity {
    BlockEntity getBlockEntity(ServerPlayNetworking.Context context);

    default BlockEntity getBlockEntity(ServerPlayNetworking.Context context, long pos) {
        return context.player().getWorld().getBlockEntity(BlockPos.fromLong(pos));
    }
}
