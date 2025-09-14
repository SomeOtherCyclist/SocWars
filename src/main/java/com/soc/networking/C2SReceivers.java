package com.soc.networking;

import com.soc.blocks.blockentities.MapBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.networking.c2s.MapBlockUpdatePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class C2SReceivers {
    public static void initialise() {
        ServerPlayNetworking.registerGlobalReceiver(MapBlockUpdatePayload.ID, (payload, context) -> {
            ServerWorld world = context.server().getWorld(payload.world());
            BlockEntity blockEntity = world.getBlockEntity(BlockPos.fromLong(payload.pos()));

            if (blockEntity instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.setRegionSize(BlockPos.fromLong(payload.regionSize()).mutableCopy());
                mapBlockEntity.setMapName(payload.mapName());
                mapBlockEntity.setMapType(GameType.fromOrdinal(payload.mapType()));

                world.getChunkManager().markForUpdate(mapBlockEntity.getPos());
            }
        });
    }
}
