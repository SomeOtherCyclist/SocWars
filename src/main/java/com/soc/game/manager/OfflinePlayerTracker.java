package com.soc.game.manager;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.util.ModCodecs;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.soc.game.manager.AbstractGameManager.resetPlayerState;

public class OfflinePlayerTracker extends PersistentState {
	public static void initialise() {
		ServerPlayerEvents.JOIN.register(player -> {
			final OfflinePlayerTracker tracker = getPersistentState(player.getWorld());
			if (!GamesManager.getInstance().isGameWithUuidRunning(tracker.playerGameMap.get(player.getUuid()))) {
				resetPlayerState(player);
			}
		});
	}

	private final Map<UUID, UUID> playerGameMap;

	public static final Codec<OfflinePlayerTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(ModCodecs.UUID, ModCodecs.UUID).fieldOf("offline_players").forGetter(tracker -> tracker.playerGameMap)
	).apply(instance, OfflinePlayerTracker::new));

	public static PersistentStateType<OfflinePlayerTracker> STATE_TYPE = new PersistentStateType<>("offline_player_tracker", OfflinePlayerTracker::new, CODEC, null);

	public static OfflinePlayerTracker getPersistentState(ServerWorld serverWorld) {
		final OfflinePlayerTracker state = serverWorld.getServer().getOverworld().getPersistentStateManager().getOrCreate(STATE_TYPE);
		state.markDirty();
		return state;
	}

	public static void onPlayerLeaveGame(ServerPlayerEntity player, UUID gameUuid) {
		getPersistentState(player.getWorld()).playerGameMap.put(player.getUuid(), gameUuid);
	}

	public static void onPlayerRejoinGame(ServerPlayerEntity player) {
		getPersistentState(player.getWorld()).playerGameMap.remove(player.getUuid());
	}

	private OfflinePlayerTracker(Map<UUID, UUID> playerGameMap) {
		this.playerGameMap = playerGameMap;
	}

	private OfflinePlayerTracker() {
		this(HashMap.newHashMap(4));
	}
}
