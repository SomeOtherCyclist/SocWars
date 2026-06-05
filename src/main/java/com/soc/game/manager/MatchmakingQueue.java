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

    private final boolean allowMultiQueue = false || true;

    private final Multimap<GameType, ServerPlayerEntity> queue;
    private final HashMap<GameType, Long> queueCompletionTime;
    private final BiConsumer<GameType, Set<ServerPlayerEntity>> queueCompletionFunction;

    private boolean dirty = false;

    public MatchmakingQueue(World world, BiConsumer<GameType, Set<ServerPlayerEntity>> queueCompletionFunction) {
		this.world = world;
		this.queue = HashMultimap.create();
        this.queueCompletionTime = new HashMap<>();
		this.queueCompletionFunction = queueCompletionFunction;
    }

    public void queuePlayer(ServerPlayerEntity player, GameType queueType) {
        if (!this.allowMultiQueue) this.unqueuePlayer(player);

        this.queue.put(queueType, player);
        this.queueCompletionTime.putIfAbsent(queueType, this.world.getTime() + 30 * 20);

        this.markDirty();
    }

    public void unqueuePlayer(ServerPlayerEntity player, GameType queueType) {
        this.queue.remove(queueType, player);
        if (this.queue.get(queueType).isEmpty()) this.queueCompletionTime.remove(queueType);

        this.markDirty();
    }

    public void unqueuePlayer(ServerPlayerEntity player) {
        for (GameType queueType : GameType.values()) {
            this.unqueuePlayer(player, queueType);
        }
    }

    public void unqueuePlayers(Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            this.unqueuePlayer(player);
        }
    }

    public void setPlayerQueues(ServerPlayerEntity player, Collection<GameType> queueTypes) {
        for (GameType queueType : GameType.values()) {
            if (queueTypes.contains(queueType)) {
                this.queuePlayer(player, queueType);
            } else {
                this.unqueuePlayer(player, queueType);
            }
        }
    }

    public Collection<ServerPlayerEntity> getPlayersInQueue(GameType queueType) {
        return this.queue.get(queueType);
    }

    public boolean isPlayerInQueue(ServerPlayerEntity player, GameType queueType) {
        return this.queue.containsEntry(queueType, player);
    }

    public boolean isPlayerInQueue(ServerPlayerEntity player) {
        return this.queue.containsValue(player);
    }

    public Collection<GameType> getPlayerQueues(ServerPlayerEntity player) {
        return this.queue.asMap().entrySet().stream().filter(entry -> entry.getValue().contains(player)).map(Map.Entry::getKey).toList();
    }

    public void checkQueues() {
        for (GameType queueType : GameType.values()) {
            final Set<ServerPlayerEntity> players = this.getLimitedPlayers(queueType);

            ifNotNull(this.queueCompletionTime.get(queueType), time -> {
                if (this.world.getTime() > time) {
                    this.queueCompletionFunction.accept(queueType, players);
                }
            });
        }

        if (this.dirty) {
            this.sendQueueProgress();
            this.dirty = false;
        }
    }

    public Set<ServerPlayerEntity> getLimitedPlayers(GameType queueType) {
        return this.queue.get(queueType).stream().limit(queueType.maxPlayers()).collect(Collectors.toSet());
    }

    public boolean allowsMultiQueue() {
        return this.allowMultiQueue;
    }

    private void markDirty() {
        this.dirty = true;
    }

    public void sendQueueProgress() {
        final QueueProgressPayload payload = new QueueProgressPayload(this.queueCompletionTime.keySet().stream().collect(Collectors.toMap(Function.identity(), queueType -> new QueueProgress(this.queue.get(queueType).size(), this.queueCompletionTime.get(queueType)))));

        for (ServerPlayerEntity player : GamesManager.getInstance().getPlayersNotInGame()) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}
