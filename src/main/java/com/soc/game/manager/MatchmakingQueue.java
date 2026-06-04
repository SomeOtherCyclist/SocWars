package com.soc.game.manager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.soc.lib.SocWarsLib;
import com.soc.networking.helper.QueueProgress;
import com.soc.networking.s2c.QueueProgressPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public class MatchmakingQueue {
    private final World world;

    private final Multimap<GameType, ServerPlayerEntity> queue;
    private final HashMap<GameType, Long> queueCompletionTime;
    private final BiConsumer<GameType, Set<ServerPlayerEntity>> queueCompletionFunction;

    public MatchmakingQueue(World world, BiConsumer<GameType, Set<ServerPlayerEntity>> queueCompletionFunction) {
		this.world = world;
		this.queue = HashMultimap.create();
        this.queueCompletionTime = new HashMap<>();
		this.queueCompletionFunction = queueCompletionFunction;
    }

    public void queuePlayer(ServerPlayerEntity player, GameType queueType) {
        this.queue.put(queueType, player);
        this.queueCompletionTime.putIfAbsent(queueType, this.world.getTime() + 30 * 20);
    }

    public void unqueuePlayer(ServerPlayerEntity player, GameType queueType) {
        this.queue.remove(queueType, player);
        if (this.queue.get(queueType).isEmpty()) this.queueCompletionTime.remove(queueType);
    }

    public void unqueuePlayer(ServerPlayerEntity player) {
        for (GameType queueType : GameType.values()) {
            this.queue.remove(queueType, player);
            if (this.queue.get(queueType).isEmpty()) this.queueCompletionTime.remove(queueType);
        }
    }

    public void setPlayerQueues(ServerPlayerEntity player, Collection<GameType> queueTypes) {
        for (GameType queueType : GameType.values()) {
            if (queueTypes.contains(queueType)) {
                this.queue.put(queueType, player);
            } else {
                this.queue.remove(queueType, player);
            }
        }
    }

    public void unqueuePlayers(Collection<ServerPlayerEntity> players) {
        for (GameType queueType : GameType.values()) {
            for (ServerPlayerEntity player : players) {
                this.queue.remove(queueType, player);
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

    public QueueProgressPayload getProgressPayload() {
        return new QueueProgressPayload(mapFromArray(GameType.values(), queueType -> new QueueProgress(this.queue.get(queueType).size(), this.queueCompletionTime.getOrDefault(queueType, -1L))));
    }

    public void checkQueues() {
        for (GameType queueType : GameType.values()) {
            final Set<ServerPlayerEntity> players = this.getLimitedPlayers(queueType);

            ifNotNullElse(this.queueCompletionTime.get(queueType), time -> {
                for (ServerPlayerEntity player : players) {
                    player.sendMessage(Text.translatable("hud.queue_time_remaining", SocWarsLib.getTimeFromSeconds((time - this.world.getTime()) * 0.05f, false)), true);
                }

                if (this.world.getTime() > time) {
                    this.queueCompletionFunction.accept(queueType, players);
                }
            }, () -> {
                for (ServerPlayerEntity player : players) {
                    player.sendMessage(Text.translatable("hud.queue_not_starting", players.size(), queueType.minPlayers()), true);
                }
            });
        }
    }

    public Set<ServerPlayerEntity> getLimitedPlayers(GameType queueType) {
        return this.queue.get(queueType).stream().limit(queueType.maxPlayers()).collect(Collectors.toSet());
    }
}
