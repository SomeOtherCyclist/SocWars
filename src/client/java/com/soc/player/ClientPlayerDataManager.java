package com.soc.player;

import com.soc.game.Kit;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientPlayerDataManager {
    public static void initialise() {
        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            instance = new ClientPlayerDataManager();
        }));
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            instance = null;
        }));
    }

    private static ClientPlayerDataManager instance = null;

    private final Map<UUID, PlayerData> playerDataMap;
    private PlayerData ownClientData;

    private ClientPlayerDataManager() {
        this.playerDataMap = new HashMap<>();
    }

    public static boolean hasCollectibleClient(int id) { //Hopefully this is mildly more optimised
        return instance != null && instance.ownClientData != null && instance.ownClientData.hasCollectible(id);
    }

    public static boolean hasKitClient(Kit kit) {
        return instance != null && instance.ownClientData != null && instance.ownClientData.ownsKit(kit.getName());
    }

    public static PlayerData getPlayerData(UUID player) {
        return instance == null ? null : instance.playerDataMap.computeIfAbsent(player, uuid -> new PlayerData());
    }

    @Nullable
    public static Morph getMorph(UUID player) {
        final PlayerData playerData = getPlayerData(player);
        return playerData == null ? null : playerData.getMorph();
    }

    public static void setPlayerData(UUID player, PlayerData playerData) {
        instance.playerDataMap.computeIfAbsent(player, uuid -> new PlayerData()).merge(playerData);
        if (MinecraftClient.getInstance().player != null && player == MinecraftClient.getInstance().player.getUuid()) instance.ownClientData = playerData;
    }

    public static void setMultiplePlayerData(Map<UUID, PlayerData> playerDataMap) {
        playerDataMap.forEach(ClientPlayerDataManager::setPlayerData);
    }
}
