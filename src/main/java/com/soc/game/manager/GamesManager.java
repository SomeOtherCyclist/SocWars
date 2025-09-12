package com.soc.game.manager;

import com.soc.game.map.BedwarsGameMap;
import com.soc.game.map.SpreadRules;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.soc.game.manager.AbstractGameManager.FOUR_TEAMS_COLOURS;

public class GamesManager {
    private static ServerWorld world;

    private static final ArrayList<AbstractGameManager> GAMES = new ArrayList<>();
    private static final MatchmakingQueue<GameType> QUEUE = new MatchmakingQueue<>();
    private static final HashMap<GameType, Float> QUEUE_PROGRESS = new HashMap<>();

    static {
        Arrays.stream(GameType.values()).forEach(queue -> QUEUE_PROGRESS.put(queue, 0f));
    }

    public static void initialise() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> world = server.getOverworld());

        ServerTickEvents.START_WORLD_TICK.register(GamesManager::tick);
    }

    public static boolean startGame(AbstractGameManager game) {
        if (game == null) return false;

        final int gameId = getNewGameId();

        if (GAMES.size() > gameId) {
            GAMES.set(gameId, game);
        } else {
            GAMES.add(gameId, game);
        }

        QUEUE.unqueuePlayers(game.getPlayers());

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

        return games.hasNext() ? i : ++i;
    }

    public static void tick(ServerWorld world) {
        checkQueues();
    }

    private static void checkQueues() {
        final float deltaTime = 0.05f * 0.1f;

        QUEUE_PROGRESS.keySet().forEach(queue -> {
            final float queueProgress = QUEUE.getQueueProgress(queue);
            QUEUE_PROGRESS.put(queue, queueProgress < Float.MIN_NORMAL ? 0f : QUEUE_PROGRESS.get(queue) + queueProgress * deltaTime);

            final Set<ServerPlayerEntity> players = Set.copyOf(QUEUE.getPlayersInQueue(queue).stream().limit(queue.maxPlayers()).toList());

            if (QUEUE_PROGRESS.get(queue) >= 1) {
                QUEUE_PROGRESS.put(queue, 0f);

                final AbstractGameManager game = switch (queue) {
                    case SKYWARS -> new SkywarsGameManager(world, players, null, getNewGameId());
                    case BEDWARS -> new BedwarsGameManager(world, players, new SpreadRules(4), getNewGameId());
                    case PROP_HUNT -> null;
                };

                startGame(game);
            }
        });
    }
}
