package com.soc.game.manager;

import com.soc.SocWars;
import com.soc.game.map.SpreadRules;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.*;

public class GamesManager {
    private static ServerWorld WORLD;

    private static final ArrayList<AbstractGameManager> GAMES = new ArrayList<>();
    private static final MatchmakingQueue<GameType> QUEUE = new MatchmakingQueue<>();
    private static final HashMap<GameType, Float> QUEUE_PROGRESS = new HashMap<>();

    static {
        Arrays.stream(GameType.values()).forEach(queue -> QUEUE_PROGRESS.put(queue, 0f));
    }

    public static void initialise() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> WORLD = server.getOverworld());

        ServerTickEvents.START_WORLD_TICK.register(GamesManager::tick);
    }

    public static boolean startGame(AbstractGameManager game) {
        if (game == null) return false;

        final int gameId = getNewGameId();

        if (GAMES.size() > gameId) {
            GAMES.set(gameId, game);
        } else {
            GAMES.add(game);
        }

        QUEUE.unqueuePlayers(game.getPlayers());

        game.startGame();

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
        if (world.getTime() % 20 == 0) { //Only update queues once per second
            checkQueues();
            QUEUE_PROGRESS.keySet().forEach(queue -> world.getServer().getPlayerManager().broadcast(Text.literal("Progress for " + queue + ": " + QUEUE_PROGRESS.get(queue)), false));
        }
    }

    private static void checkQueues() {
        QUEUE_PROGRESS.keySet().forEach(queue -> {
            final float queueProgress = QUEUE.getQueueProgress(queue);
            QUEUE_PROGRESS.put(queue, queueProgress < Float.MIN_NORMAL ? 0f : QUEUE_PROGRESS.get(queue) + queueProgress); //Update the queue progress of every queue

            final Set<ServerPlayerEntity> players = Set.copyOf(QUEUE.getPlayersInQueue(queue).stream().limit(queue.maxPlayers()).toList()); //Cap the number of players to send into a game to the queue's max player count

            if (QUEUE_PROGRESS.get(queue) >= 0.02f) {
                QUEUE_PROGRESS.put(queue, 0f); //Reset the queue progress

                final AbstractGameManager game = switch (queue) {
                    case SKYWARS -> new SkywarsGameManager(WORLD, players, null, getNewGameId());
                    case BEDWARS -> new BedwarsGameManager(WORLD, players, new SpreadRules(4), getNewGameId());
                    case PROP_HUNT -> null; //Maybe get around to writing some of the game logic for prop hunt
                };

                boolean startedGame = startGame(game);
                if (!startedGame) SocWars.LOGGER.warn("Failed to start game {}", game.getGameId());
            }
        });
    }

    public static void queuePlayer(ServerPlayerEntity player, GameType queue) {
        QUEUE.queuePlayer(player, queue);
    }

    public static void unqueuePlayer(ServerPlayerEntity player, GameType queue) {
        if (!QUEUE.isPlayerInQueue(player, queue)) QUEUE.unqueuePlayer(player);
    }
}
