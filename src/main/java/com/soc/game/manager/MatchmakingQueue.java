package com.soc.game.manager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.soc.networking.helper.QueueProgress;
import com.soc.networking.s2c.QueueProgressPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public class MatchmakingQueue {
    private final World world;

    private final boolean allowMultiQueue = false;

    private final Multimap<GameType, ServerPlayerEntity> queue;
    private final HashMap<GameType, Long> queueCompletionTime;
    private final BiConsumer<GameType, Set<ServerPlayerEntity>> queueCompletionFunction;
    private final Set<GameType> allowedSinglePlayerQueues;

    private boolean dirty = false;

    public MatchmakingQueue(World world, BiConsumer<GameType, Set<ServerPlayerEntity>> queueCompletionFunction) {
		this.world = world;
		this.queue = HashMultimap.create();
        this.queueCompletionTime = new HashMap<>();
		this.queueCompletionFunction = queueCompletionFunction;
        this.allowedSinglePlayerQueues = new HashSet<>(GameType.values().length);
    }

    public void queuePlayer(ServerPlayerEntity player, GameType gameType) {
        if (!this.allowMultiQueue) this.unqueuePlayer(player);

        this.queue.put(gameType, player);
        this.queueCompletionTime.putIfAbsent(gameType, this.world.getTime() + 30 * 20);

        this.markDirty();
    }

    public void unqueuePlayer(ServerPlayerEntity player, GameType gameType) {
        this.queue.remove(gameType, player);
        if (this.queue.get(gameType).isEmpty()) this.queueCompletionTime.remove(gameType);

        this.markDirty();
    }

    public void unqueuePlayer(ServerPlayerEntity player) {
        for (GameType gameType : GameType.values()) {
            this.unqueuePlayer(player, gameType);
        }
    }

    public void unqueuePlayers(Collection<ServerPlayerEntity> players, GameType gameType) {
        for (ServerPlayerEntity player : players) {
            this.unqueuePlayer(player, gameType);
        }
    }

    public void unqueuePlayers(Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            this.unqueuePlayer(player);
        }
    }

    public void setPlayerQueues(ServerPlayerEntity player, Collection<GameType> gameTypes) {
        for (GameType gameType : GameType.values()) {
            if (gameTypes.contains(gameType)) {
                this.queuePlayer(player, gameType);
            } else {
                this.unqueuePlayer(player, gameType);
            }
        }
    }

    public Collection<ServerPlayerEntity> getPlayersInQueue(GameType gameType) {
        return this.queue.get(gameType);
    }

    public boolean isPlayerInQueue(ServerPlayerEntity player, GameType gameType) {
        return this.queue.containsEntry(gameType, player);
    }

    public boolean isPlayerInQueue(ServerPlayerEntity player) {
        return this.queue.containsValue(player);
    }

    public Collection<GameType> getPlayerQueues(ServerPlayerEntity player) {
        return this.queue.asMap().entrySet().stream().filter(entry -> entry.getValue().contains(player)).map(Map.Entry::getKey).toList();
    }

    public void checkQueues() {
        for (GameType gameType : GameType.values()) {
            final Set<ServerPlayerEntity> players = this.getLimitedPlayers(gameType);

            ifNotNull(this.queueCompletionTime.get(gameType), time -> {
                if (this.world.getTime() > time) {
                    if (players.size() >= gameType.minPlayers() || this.allowedSinglePlayerQueues.contains(gameType)) {
                        this.allowedSinglePlayerQueues.remove(gameType);
                        this.queueCompletionFunction.accept(gameType, players);
                    } else {
                        this.unqueuePlayers(players, gameType);
                    }
                }
            });
        }

        if (this.dirty) {
            this.sendQueueProgress();
            this.dirty = false;
        }
    }

    public Set<ServerPlayerEntity> getLimitedPlayers(GameType gameType) {
        return this.queue.get(gameType).stream().limit(gameType.maxPlayers()).collect(Collectors.toSet());
    }

    public boolean allowsMultiQueue() {
        return this.allowMultiQueue;
    }

    private void markDirty() {
        this.dirty = true;
    }

    public void sendQueueProgress() {
        final QueueProgressPayload payload = new QueueProgressPayload(this.queueCompletionTime.keySet().stream().collect(Collectors.toMap(Function.identity(), gameType -> new QueueProgress(this.queue.get(gameType).size(), this.queueCompletionTime.get(gameType)))));

        for (ServerPlayerEntity player : GamesManager.getInstance().getPlayersNotInGame()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public void allowSinglePlayer(GameType gameType) {
        this.allowedSinglePlayerQueues.add(gameType);
    }

    public void disallowSinglePlayer(GameType gameType) {
        this.allowedSinglePlayerQueues.remove(gameType);
    }
}
