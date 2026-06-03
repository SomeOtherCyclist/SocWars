package com.soc.game.manager;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.soc.SocWars;
import com.soc.events.ModEvents;
import com.soc.game.map.SpreadRules;
import com.soc.lib.SocWarsLib;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;

public class GamesManager {
    private static final GamesManager INSTANCE = new GamesManager();

    public static final float QUEUE_PROGRESS_THRESHOLD = 1f;
    public static final int QUEUE_CHECK_INTERVAL = 20;

    private ServerWorld world;

    private final ArrayList<AbstractGameManager<?, ?, ?>> games = new ArrayList<>();
    private final ConcurrentHashMap<UUID, Integer> playerGameLookup = new ConcurrentHashMap<>();

    private final MatchmakingQueue<GameType> queue = new MatchmakingQueue<>();
    private final HashMap<GameType, Float> queueProgress = new HashMap<>();

    private GamesManager() {
        for (GameType queue : GameType.values()) this.queueProgress.put(queue, 0f);
        this.initialiseEvents();
    }

    public static void initialise() {}

    public static GamesManager getInstance() {
        return INSTANCE;
    }

    public void initialiseEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.world = server.getOverworld());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.endAllGames());
        ServerPlayerEvents.LEAVE.register(player -> {
            if (!player.getWorld().getServer().isDedicated()) this.endAllGames();
        });
        ServerTickEvents.START_SERVER_TICK.register(this::tick);

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) ->
                this.getGame(entity).map(game -> game.onPlayerDeath((ServerPlayerEntity) entity, source, amount)).orElse(true)
        );
        ModEvents.ON_PLAYER_DAMAGE_TAKEN.register((player, source, amount) ->
                this.getGame(player).map(game -> game.onPlayerDamage(player, source, amount)).orElse(true)
        );
        ModEvents.ON_CHEST_OPENED.register((player, pos) ->
                this.getGame(player).map(game -> game.onChestOpened(player, pos)).orElse(true)
        );
        ModEvents.ON_ITEM_PICKUP.register((player, stack) ->
                this.getGame(player).ifPresent(game -> game.onItemPickup(player, stack))
        );
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) ->
                state.isIn(BlockTags.BEDS) ? this.getGame(player).map(game -> game.onBedBroken((ServerPlayerEntity) player, pos)).orElse(true) : this.getGame(player).map(game -> game.onBlockBroken((ServerPlayerEntity) player, pos, state, blockEntity)).orElse(true)
        );
        ModEvents.ON_BLOCK_PLACED.register((player, pos, context) ->
                this.getGame(player).map(game -> game.onBlockPlaced(player, pos, context)).orElse(ActionResult.PASS)
        );
        ServerPlayerEvents.JOIN.register(player ->
                this.getGame(player).ifPresent(game -> game.onPlayerJoin(player))
        );
        ModEvents.ON_CRAFTING_TABLE_OPENED.register((player, pos) ->
                this.getGame(player).map(game -> game.onCraftingTableOpened(player, pos)).orElse(true)
        );
        ModEvents.ON_FURNACE_OPENED.register((player, pos) ->
                this.getGame(player).map(game -> game.onFurnaceOpened(player, pos)).orElse(true)
        );
        ModEvents.ON_PLAYER_MORPHED.register((player, morph) ->
                this.getGame(player).map(game -> game.onPlayerMorphed(player, morph)).orElse(true)
        );
    }

    private void endAllGames() {
        this.games.forEach(game -> {
                if (game != null) game.endGame(true);
        });
    }

    public void returnAllPlayersToLobby() {
        this.games.forEach(game -> {
            if (game != null) game.sendPlayersToLobby();
        });
    }

    public boolean startGame(AbstractGameManager<?, ?, ?> game) {
        if (game == null) return false;

        if (this.games.size() > game.getGameId()) {
            this.games.set(game.getGameId(), game);
        } else {
            this.games.add(game);
        }

        this.queue.unqueuePlayers(game.getPlayers());

        game.startGame();
        game.getPlayers().forEach(player -> this.playerGameLookup.put(player.getUuid(), game.getGameId())); //Bit of gross bookkeeping

        return true;
    }

    /** This should only ever be called by an ending {@link com.soc.game.manager.AbstractGameManager}
    */
    public void endGame(int gameId) {
        this.playerGameLookup.forEach((player, id) -> {
            if (id == gameId) this.playerGameLookup.remove(player);
        }); //Tail end of the gross bookkeeping
        this.games.set(gameId, null);
    }

    public Optional<AbstractGameManager<?, ?, ?>> getGame(UUID uuid) {
        final Integer id = this.playerGameLookup.get(uuid);
        return id == null ? Optional.empty() : Optional.of(this.games.get(id));
    }

    public Optional<AbstractGameManager<?, ?, ?>> getGame(Entity entity) {
        if (entity == null) return Optional.empty();
        return this.getGame(entity.getUuid());
    }

    public Optional<AbstractGameManager<?, ?, ?>> getGame(int gameId) {
        if (gameId < 0 || gameId >= this.games.size()) return Optional.empty();
        return Optional.ofNullable(this.games.get(gameId));
    }

    public List<Integer> getActiveGameIds() {
        return range(0, this.games.size()).asLongStream().filter(id -> this.games.get((int)id) != null).mapToObj(id -> (int)id).toList();
    }

    public Collection<Suggestion> getGameIdSuggestions(int cursor) {
        return range(0, this.games.size()).asLongStream().filter(id -> this.games.get((int)id) != null).mapToObj(id -> {
            final String suggestion = String.valueOf(id);
            return new Suggestion(new StringRange(cursor, cursor + suggestion.length()), suggestion);
        }).toList();
    }

    private int getNewGameId() {
        for (int i = 0; i < this.games.size(); i++) {
            if (this.games.get(i) == null) return i;
        }
        return this.games.size();
    }

    public void tick(MinecraftServer server) {
        this.games.forEach(game -> {
            if (game != null) game.tick();
        });

        if (this.world.getTime() % QUEUE_CHECK_INTERVAL == 0) { //Only update queues once per second
            this.checkQueues();
        }
    }

    private void checkQueues() {
        this.queueProgress.keySet().forEach(queueType -> {
            final float currentProgress = this.queueProgress.get(queueType);
            final float queueProgressDelta = this.queue.getQueueProgress(queueType) * QUEUE_CHECK_INTERVAL;

            final Set<ServerPlayerEntity> players = this.queue.getPlayersInQueue(queueType).stream().limit(queueType.maxPlayers()).collect(Collectors.toSet()); //Cap the number of players to send into options game to the queueType's max player count

            this.queueProgress.put(queueType, queueProgressDelta < Float.MIN_NORMAL || players.size() < queueType.minPlayers() ? 0f : currentProgress + queueProgressDelta); //Update the queueType progress of every queueType

            final int remainingTime = (int)((QUEUE_PROGRESS_THRESHOLD - currentProgress) / queueProgressDelta);
            players.forEach(player -> player.sendMessage(remainingTime > 1000f ? Text.translatable("hud.queue_not_starting", players.size(), queueType.minPlayers()) : Text.translatable("hud.queue_time_remaining", SocWarsLib.getTimeFromTicks(remainingTime, false)), true));

            if (currentProgress >= QUEUE_PROGRESS_THRESHOLD) {
                this.finishQueue(queueType, players);
            }
        });
    }

    private void finishQueue(GameType queue, Set<ServerPlayerEntity> players) {
        this.queueProgress.put(queue, 0f); //Reset the queueType progress

        final int gameId = this.getNewGameId();

        final AbstractGameManager<?, ?, ?> game = switch (queue) {
            case SKYWARS -> new SkywarsGameManager(this.world, players, null, gameId, SkywarsGameManager.Settings.DEFAULT);
            case BEDWARS -> new BedwarsGameManager(this.world, players, new SpreadRules(4), gameId);
            case PROP_HUNT -> new PropHuntGameManager(this.world, players, null, gameId);
            case HIDE_AND_SEEK -> new HideAndSeekGameManager(this.world, players, null, gameId);
        };

        final boolean startedGame = this.startGame(game);
        if (!startedGame) SocWars.LOGGER.warn("Failed to start game {}", game.getGameId());
    }

    public void queuePlayer(ServerPlayerEntity player, GameType queue) {
        this.queue.queuePlayer(player, queue);
    }

    public void unqueuePlayer(ServerPlayerEntity player) {
        this.queue.unqueuePlayer(player);
    }

    public List<ServerPlayerEntity> getPlayersNotInGame() {
        return this.world.getPlayers().stream().filter(player -> !this.queue.isPlayerInQueue(player)).toList();
    }
}
