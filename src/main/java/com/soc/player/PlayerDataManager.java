package com.soc.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.events.ModEvents;
import com.soc.networking.s2c.AllSyncPlayerDataPayload;
import com.soc.networking.s2c.SinglePlayerDataPayload;
import com.soc.util.ModCodecs;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.soc.lib.SocWarsLib.ifNotNull;

public class PlayerDataManager extends PersistentState {
    public static void initialise() {
        ServerPlayerEvents.JOIN.register(PlayerDataManager::sendData);
        ModEvents.ON_COLLECTIBLE_BLOCK_REPLACED.register((id, world) -> getPersistentState(world).playerDataMap.forEach((uuid, playerData) -> {
            ifNotNull(world.getPlayerByUuid(uuid), player -> sendData((ServerPlayerEntity)player));
            playerData.resetCollectible(id);
        }));
    }

    public static void sendData(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new SinglePlayerDataPayload(player.getUuid(), PlayerDataManager.getPlayerData(player)));
    }

    public static void sendDataToAll(MinecraftServer server) {
        final AllSyncPlayerDataPayload payload = new AllSyncPlayerDataPayload(getPersistentState(server.getOverworld()).playerDataMap);
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			ServerPlayNetworking.send(player, payload);
		}
	}

    public static final Codec<PlayerDataManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(ModCodecs.UUID, PlayerData.CODEC).fieldOf("player_data_map").forGetter(PlayerDataManager::getPlayerDataMap)
    ).apply(instance, PlayerDataManager::new));

    public static PersistentStateType<PlayerDataManager> STATE_TYPE = new PersistentStateType<>("player_data_manager", PlayerDataManager::new, CODEC, null);

    private final Map<UUID, PlayerData> playerDataMap;

    private PlayerDataManager(Map<UUID, PlayerData> playerDataMap) {
        this.playerDataMap = new HashMap<>(playerDataMap);
    }

    private PlayerDataManager() {
        this.playerDataMap = new HashMap<>();
    }

    public Map<UUID, PlayerData> getPlayerDataMap() { return this.playerDataMap; }

    public static PlayerData getPlayerData(ServerPlayerEntity player) {
        return player == null ? null : getPersistentState(player.getWorld()).playerDataMap.computeIfAbsent(player.getUuid(), uuid2 -> new PlayerData());
    }

    public static PlayerData getSideLocalPlayerData(PlayerEntity player) {
        return getPlayerData((ServerPlayerEntity)player); //I'm gonna mixinto this on the client so this cast should never be a problem. Hopefully. Maybe it has issues with integrated servers. I don't know mate. Also yes I know this is disgusting
    }

    public static PlayerDataManager getPersistentState(ServerWorld serverWorld) {
        final PlayerDataManager state = serverWorld.getServer().getOverworld().getPersistentStateManager().getOrCreate(STATE_TYPE);
        state.markDirty();
        return state;
    }

    public static boolean collectDoubloons(PlayerEntity player, int doubloons) {
        final ScoreboardObjective objective = player.getScoreboard().getNullableObjective("Doubloons");
        if (objective == null) return false;

        player.getScoreboard().getOrCreateScore(ScoreHolder.fromProfile(player.getGameProfile()), objective).incrementScore(doubloons);
        return true;
    }
}
