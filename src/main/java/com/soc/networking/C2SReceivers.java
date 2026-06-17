package com.soc.networking;

import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.blocks.blockentities.MapBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.game.manager.GamesManager;
import com.soc.items.util.OnAttackButtonPressed;
import com.soc.networking.c2s.*;
import com.soc.networking.s2c.OpenQueueScreenPayload;
import com.soc.networking.s2c.QueuePayload;
import com.soc.player.PlayerDataManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class C2SReceivers {
    public static void initialise() {
        queues();
        ServerPlayNetworking.registerGlobalReceiver(MapBlockUpdatePayload.ID, (payload, context) -> {
            if (payload.getBlockEntity(context) instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.setRegionSize(BlockPos.fromLong(payload.regionSize()).mutableCopy());
                mapBlockEntity.setMapName(payload.mapName());
                mapBlockEntity.setMapType(GameType.fromOrdinal(payload.mapType()));
                mapBlockEntity.setBlockProtection(payload.blockProtection());
                mapBlockEntity.setFields(payload.fields());

                context.player().getWorld().getChunkManager().markForUpdate(mapBlockEntity.getPos());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(MapBlockStructureCheckPayload.ID, (payload, context) -> {
            if (payload.getBlockEntity(context) instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.checkStructure();

                context.player().getWorld().getChunkManager().markForUpdate(mapBlockEntity.getPos());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(MapBlockSaveMapPayload.ID, (payload, context) -> {
            if (payload.getBlockEntity(context) instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.saveMap(context.player());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(KitBlockUpdatePayload.ID, (payload, context) -> {
            if (payload.getBlockEntity(context) instanceof KitBlockEntity kitBlockEntity) {
                kitBlockEntity.update(payload);
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(KitSelectionPayload.ID, (payload, context) -> {
            if (payload.getBlockEntity(context) instanceof KitBlockEntity kitBlockEntity) {
                final boolean playerOwnsKit = PlayerDataManager.getPlayerData(context.player()).setKits(kitBlockEntity.getKit(), payload.selectedGameTypes());

                final Text kitSelectionMessage = playerOwnsKit ? KitBlockEntity.getKitSelectionMessage(payload.selectedGameTypes(), kitBlockEntity.getKit()) : Text.translatable("message.kit_selection.not_owned");
                context.player().sendMessage(kitSelectionMessage);
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(OnAttackButtonPressedPayload.ID, (payload, context) -> {
            ((OnAttackButtonPressed)payload.stack().getItem()).onAttackButtonPressed(context.player());
        });
        ServerPlayNetworking.registerGlobalReceiver(BuyKitPayload.ID, (payload, context) -> {
            PlayerDataManager.buyKit(context.player(), payload);
        });
    }

    private static void queues() {
        ServerPlayNetworking.registerGlobalReceiver(QueuePayload.ID, (payload, context) -> {
            GamesManager.getInstance().setPlayerQueues(context.player(), payload.queues());
        });
        ServerPlayNetworking.registerGlobalReceiver(RequestOpenQueueScreenPayload.ID, (payload, context) -> {
            final GamesManager gamesManager = GamesManager.getInstance();
            if (!gamesManager.isPlayerInGame(context.player())) {
                ServerPlayNetworking.send(context.player(), new OpenQueueScreenPayload(gamesManager.getPlayerQueues(context.player()), gamesManager.allowsMultiQueue()));
            }
        });
    }
}
