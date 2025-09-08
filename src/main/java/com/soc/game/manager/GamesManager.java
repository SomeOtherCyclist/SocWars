package com.soc.game.manager;

import com.soc.networking.s2c.JoinQueuePayload;
import com.soc.networking.s2c.LeaveQueuePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.apache.commons.lang3.ArrayUtils.toArray;

public class GamesManager {
    public enum GameType {
        SKYWARS,
        BEDWARS,
        PROP_HUNT;

        GameType fromNatural(String string) {
            return GameType.valueOf(string.replace(' ', '_').toUpperCase());
        }

        String toNatural() {
            return StringUtils.capitalize(this.toString());
        }
    }

    public static final HashMap<Identifier, AbstractGameManager> GAMES = new HashMap<>();
    private static final HashMap<ServerPlayerEntity, GameType> QUEUE = new HashMap<>();

    public static boolean queuePlayer(ServerPlayerEntity player, GameType gameType) {
        if (QUEUE.containsKey(player)) return false;

        ServerPlayNetworking.send(player, new JoinQueuePayload(gameType.toString()));
        QUEUE.put(player, gameType);

        return true;
    }

    public static boolean unqueuePlayer(ServerPlayerEntity player) {
        if (!QUEUE.containsKey(player)) return false;

        ServerPlayNetworking.send(player, new LeaveQueuePayload(QUEUE.get(player).toString()));
        QUEUE.remove(player);

        return true;
    }

    public static ArrayList<ServerPlayerEntity> getPlayersInQueue(GameType gameType) {
        final ArrayList<ServerPlayerEntity> players = new ArrayList<>(QUEUE.size()); //Probably not great for very large player counts but this should never deal with more than a few players at a time
        QUEUE.entrySet().iterator().forEachRemaining(entry -> {
            if (entry.getValue() == gameType) players.add(entry.getKey());
        });

        return players;
    }
}
