package com.soc.networking;

import com.soc.SocWars;
import com.soc.blocks.blockentities.MapBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.networking.c2s.MapBlockSaveMapPayload;
import com.soc.networking.c2s.MapBlockStructureCheckPayload;
import com.soc.networking.c2s.MapBlockUpdatePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class C2SReceivers {
    public static void initialise() {
        ServerPlayNetworking.registerGlobalReceiver(MapBlockUpdatePayload.ID, (payload, context) -> {
            BlockEntity blockEntity = payload.getBlockEntity(context);

            if (blockEntity instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.setRegionSize(BlockPos.fromLong(payload.regionSize()).mutableCopy());
                mapBlockEntity.setMapName(payload.mapName());
                mapBlockEntity.setMapType(GameType.fromOrdinal(payload.mapType()));

                context.player().getWorld().getChunkManager().markForUpdate(mapBlockEntity.getPos());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(MapBlockStructureCheckPayload.ID, (payload, context) -> {
            BlockEntity blockEntity = payload.getBlockEntity(context);

            if (blockEntity instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.checkStructure();

                context.player().getWorld().getChunkManager().markForUpdate(mapBlockEntity.getPos());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(MapBlockSaveMapPayload.ID, (payload, context) -> {
            BlockEntity blockEntity = payload.getBlockEntity(context);

            if (blockEntity instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.saveMap(context.player());
            }
        });
    }
}
