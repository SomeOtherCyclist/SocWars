package com.soc.game.manager;

import com.soc.SocWars;
import com.soc.game.map.SpreadRules;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GamesManager {
    private static final GamesManager INSTANCE = new GamesManager();

    private ServerWorld world;

    private final ArrayList<AbstractGameManager> games = new ArrayList<>();
    private final HashMap<ServerPlayerEntity, Integer> playerGameLookup = new HashMap<>();

    private final MatchmakingQueue<GameType> queue = new MatchmakingQueue<>();
    private final HashMap<GameType, Float> queueProgress = new HashMap<>();

    private GamesManager() {
        Arrays.stream(GameType.values()).forEach(queue -> this.queueProgress.put(queue, 0f));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.world = server.getOverworld());
        ServerTickEvents.START_WORLD_TICK.register(this::tick);

        this.initialiseEvents();
    }

    public static void initialise() {}

    public static GamesManager getInstance() {
        return INSTANCE;
    }

    public void initialiseEvents() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            return this.getGame(entity).map(game -> game.onPlayerDeath((ServerPlayerEntity) entity, source, amount)).orElse(true);
        });
    }

    public boolean startGame(AbstractGameManager game) {
        if (game == null) return false;

        if (this.games.size() > game.getGameId()) {
            this.games.set(game.getGameId(), game);
        } else {
            this.games.add(game);
        }

        this.queue.unqueuePlayers(game.getPlayers());

        game.startGame();
        game.getPlayers().forEach(player -> this.playerGameLookup.put(player, game.getGameId())); //Bit of gross bookkeeping

        return true;
    }

    public void endGame(int gameId) {
        this.games.set(gameId, null);
        this.playerGameLookup.forEach(this.playerGameLookup::remove); //Tail end of the gross bookkeeping
    }

    public Optional<AbstractGameManager> getGame(LivingEntity entity) {
        final Integer id = this.playerGameLookup.get(entity); //Hilarious abuse of HashMap#Get
        return Optional.ofNullable(id).map(this.games::get);
    }

    private int getNewGameId() {
        final Iterator<AbstractGameManager> games = this.games.iterator();

        int i = 0;
        while (games.hasNext() && games.next() != null) {
            i++;
        }

        return games.hasNext() ? i : ++i;
    }

    public void tick(ServerWorld world) {
        this.games.forEach(game -> {
            if (game != null) game.tick();
        });

        if (world.getTime() % 20 == 0) { //Only update queues once per second
            this.checkQueues();
        }
    }

    private void checkQueues() {
        this.queueProgress.keySet().forEach(queue -> {
            final float queueProgress = this.queue.getQueueProgress(queue);
            this.queueProgress.put(queue, queueProgress < Float.MIN_NORMAL ? 0f : this.queueProgress.get(queue) + queueProgress); //Update the queue progress of every queue

            final Set<ServerPlayerEntity> players = Set.copyOf(this.queue.getPlayersInQueue(queue).stream().limit(queue.maxPlayers()).toList()); //Cap the number of players to send into a game to the queue's max player count

            if (this.queueProgress.get(queue) >= 0.02f) {
                this.queueProgress.put(queue, 0f); //Reset the queue progress

                final AbstractGameManager game = switch (queue) {
                    case SKYWARS -> new SkywarsGameManager(world, players, null, this.getNewGameId());
                    case BEDWARS -> new BedwarsGameManager(world, players, new SpreadRules(4), this.getNewGameId());
                    case PROP_HUNT -> null; //Maybe get around to writing some of the game logic for prop hunt
                };

                boolean startedGame = startGame(game);
                if (!startedGame) SocWars.LOGGER.warn("Failed to start game {}", game.getGameId());
            }
        });
    }

    public void queuePlayer(ServerPlayerEntity player, GameType queue) {
        this.queue.queuePlayer(player, queue);
    }

    public void unqueuePlayer(ServerPlayerEntity player, GameType queue) {
        if (!this.queue.isPlayerInQueue(player, queue)) this.queue.unqueuePlayer(player);
    }
}
