package com.soc.players;

import com.soc.SocWars;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager {
    public static void initialise() {
        net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.JOIN.register((entity) -> {
            HashMap<UUID, PlayerData> playerDataMap = PlayerDataManager.getPlayerDataMap();
            if (!playerDataMap.containsKey(entity.getUuid())) {
                playerDataMap.put(entity.getUuid(), new PlayerData());
            }
        });
    }

    private static HashMap<UUID, PlayerData> playerDataMap = HashMap.newHashMap(10);

    public static HashMap<UUID, PlayerData> getPlayerDataMap() { return playerDataMap; }

    public static PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }
}
