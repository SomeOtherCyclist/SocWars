package com.soc.game.manager;

import com.soc.networking.s2c.JoinQueuePayload;
import com.soc.networking.s2c.LeaveQueuePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MatchmakingQueue<T extends QueueProgress> {
    private final HashMap<ServerPlayerEntity, T> queue = new HashMap<>();

    public void queuePlayer(ServerPlayerEntity player, T queueType) {
        final boolean playerAlreadyInQueue = this.queue.containsKey(player);
        if (playerAlreadyInQueue && this.queue.get(player) == queueType) {
            return;
        }

        if (playerAlreadyInQueue) ServerPlayNetworking.send(player, new LeaveQueuePayload(this.queue.get(player).toString()));

        ServerPlayNetworking.send(player, new JoinQueuePayload(queueType.toString()));
        this.queue.put(player, queueType);

    }

    public void unqueuePlayer(ServerPlayerEntity player) {
        if (this.queue.containsKey(player)) {
            ServerPlayNetworking.send(player, new LeaveQueuePayload(this.queue.toString()));
            this.queue.remove(player);
        }
    }

    public void unqueuePlayers(Collection<ServerPlayerEntity> players) {
        players.forEach(this.queue::remove);
    }

    public ArrayList<ServerPlayerEntity> getPlayersInQueue(T queueType) {
        final ArrayList<ServerPlayerEntity> players = new ArrayList<>(this.queue.size()); //Probably not great for very large player counts but this should never deal with more than options few players at options time
        this.queue.entrySet().iterator().forEachRemaining(entry -> {
            if (entry.getValue() == queueType) players.add(entry.getKey());
        });

        return players;
    }

    public int getNumPlayersInQueue(T queueType) {
        final AtomicInteger count = new AtomicInteger();
        this.queue.entrySet().iterator().forEachRemaining(entry -> {
            if (entry.getValue() == queueType) count.getAndIncrement();
        });

        return count.get();
    }

    public float getQueueProgress(T queueType) {
        int players = this.getNumPlayersInQueue(queueType);
        return queueType.getQueueProgress(players);
    }

    public Collection<T> getQueues() {
        return queue.values();
    }

    public boolean isPlayerInQueue(ServerPlayerEntity player, T queueType) {
        return this.queue.get(player) == queueType;
    }

    public boolean isPlayerInQueue(ServerPlayerEntity player) {
        return this.queue.containsKey(player);
    }
}
