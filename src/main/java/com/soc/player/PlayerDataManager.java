package com.soc.player;

import com.soc.SocWars;
import com.soc.networking.s2c.PlayerDataPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager {
    public static void initialise() {
        net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.JOIN.register((entity) -> {
            HashMap<UUID, PlayerData> playerDataMap = PlayerDataManager.getPlayerDataMap();
            if (!playerDataMap.containsKey(entity.getUuid())) {
                playerDataMap.put(entity.getUuid(), new PlayerData());
            }

            final PlayerDataPayload playerDataPayload = new PlayerDataPayload(PlayerDataManager.getPlayerData(entity.getUuid()));
            ServerPlayNetworking.send(entity, playerDataPayload);

            SocWars.LOGGER.info("Player {} joined with UUID {}", entity.getName().getString(), entity.getUuidAsString());
        });
    }

    private static final HashMap<UUID, PlayerData> playerDataMap = HashMap.newHashMap(10);

    public static HashMap<UUID, PlayerData> getPlayerDataMap() { return playerDataMap; }

    public static PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }
    public static PlayerData getPlayerData(PlayerEntity player) {
        return getPlayerData(player.getUuid());
    }

    public static void setPlayerData(UUID uuid, PlayerData playerData) {
        playerDataMap.put(uuid, playerData);
    }

    public static void setPlayerData(PlayerEntity player, PlayerData playerData) {
        setPlayerData(player.getUuid(), playerData);
    }

    public static boolean collectDoubloons(PlayerEntity player, int doubloons) {
        ScoreboardObjective objective = player.getScoreboard().getNullableObjective("Doubloons");
        if (objective == null) return false;

        player.getScoreboard().getOrCreateScore(ScoreHolder.fromProfile(player.getGameProfile()), objective).incrementScore(doubloons);
        return true;
    }
}
