package com.soc.game.manager;

import com.soc.networking.s2c.JoinQueuePayload;
import com.soc.networking.s2c.LeaveQueuePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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

    private static final ArrayList<AbstractGameManager> GAMES = new ArrayList<>();
    private static final HashMap<ServerPlayerEntity, GameType> QUEUE = new HashMap<>();

    public static boolean queuePlayer(ServerPlayerEntity player, GameType gameType) {
        if (!QUEUE.containsKey(player) || QUEUE.get(player) == gameType) {
            return false;
        }

        ServerPlayNetworking.send(player, new JoinQueuePayload(gameType.toString()));
        QUEUE.put(player, gameType);

        return true;
    }

    public static boolean unqueuePlayer(ServerPlayerEntity player) {
        if (!QUEUE.containsKey(player)) return false;

        ServerPlayNetworking.send(player, new LeaveQueuePayload(QUEUE.toString()));
        QUEUE.remove(player);

        return true;
    }

    public static void unqueuePlayers(Collection<ServerPlayerEntity> players) {
        players.forEach(QUEUE::remove);
    }

    public static ArrayList<ServerPlayerEntity> getPlayersInQueue(GameType gameType) {
        final ArrayList<ServerPlayerEntity> players = new ArrayList<>(QUEUE.size()); //Probably not great for very large player counts but this should never deal with more than a few players at a time
        QUEUE.entrySet().iterator().forEachRemaining(entry -> {
            if (entry.getValue() == gameType) players.add(entry.getKey());
        });

        return players;
    }

    public static boolean startGame(AbstractGameManager game) {
        if (game == null) return false;

        final int gameId = getNewGameId();

        if (GAMES.size() > gameId) {
            GAMES.set(gameId, game);
        } else {
            GAMES.add(gameId, game);
        }

        unqueuePlayers(game.getPlayers());

        return true;
    }

    public static void endGame(int gameId) {
        GAMES.set(gameId, null);
    }

    private static int getNewGameId() {
        final Iterator<AbstractGameManager> games = GAMES.iterator();

        int i = 0;
        while (games.hasNext() && games.next() != null) {
            i++;
        }

        return ++i;
    }
}
