package com.soc.game.manager;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.soc.SocWars;
import com.soc.events.ModEvents;
import com.soc.game.map.SpreadRules;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.IntStream.range;

public class GamesManager {
    private static final GamesManager INSTANCE = new GamesManager();

    private ServerWorld world;
    private MatchmakingQueue queue;

    private final ArrayList<AbstractGameManager<?, ?, ?>> games = new ArrayList<>();
    private final ConcurrentHashMap<UUID, Integer> playerGameLookup = new ConcurrentHashMap<>();

    private GamesManager() {
        this.initialiseEvents();
    }

    public static void initialise() {}

    public static GamesManager getInstance() {
        return INSTANCE;
    }

    public void initialiseEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.world = server.getOverworld();
            this.queue = new MatchmakingQueue(this.world, this::finishQueue);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.endAllGames());

        ServerTickEvents.START_SERVER_TICK.register(this::tick);

        ServerPlayerEvents.LEAVE.register(player -> {
            if (!player.getWorld().getServer().isDedicated()) this.endAllGames();
        });
        ServerPlayerEvents.JOIN.register(player -> {
            this.getGame(player).ifPresent(game -> game.onPlayerJoin(player));
            if (!this.isPlayerInGame(player)) ServerPlayNetworking.send(player, this.queue.getQueueProgressPayload());
        });

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

        this.queue.sendQueueProgress();
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

        this.queue.checkQueues();
    }

    public void queuePlayer(ServerPlayerEntity player, GameType queue) {
        this.queue.queuePlayer(player, queue);
    }

    public void unqueuePlayer(ServerPlayerEntity player) {
        this.queue.unqueuePlayer(player);
    }

    public void unqueuePlayer(ServerPlayerEntity player, GameType queueType) {
        this.queue.unqueuePlayer(player, queueType);
    }

    public void setPlayerQueues(ServerPlayerEntity player, Collection<GameType> queues) {
        this.queue.setPlayerQueues(player, queues);
    }

    public boolean isPlayerInQueue(ServerPlayerEntity player) {
        return this.queue.isPlayerInQueue(player);
    }

    public boolean isPlayerInQueue(ServerPlayerEntity player, GameType gameType) {
        return this.queue.isPlayerInQueue(player, gameType);
    }

    public Collection<GameType> getPlayerQueues(ServerPlayerEntity player) {
        return this.queue.getPlayerQueues(player);
    }

    public List<ServerPlayerEntity> getPlayersNotInQueue() {
        return this.world.getPlayers().stream().filter(player -> !this.queue.isPlayerInQueue(player)).toList();
    }

    public List<ServerPlayerEntity> getPlayersNotInGame() {
        return this.world.getPlayers().stream().filter(player -> !this.playerGameLookup.containsKey(player.getUuid())).toList();
    }

    private boolean finishQueue(GameType queueType, Set<ServerPlayerEntity> players) {
        if (players.isEmpty()) return false;

        final int gameId = this.getNewGameId();

        final AbstractGameManager<?, ?, ?> game = switch (queueType) {
            case SKYWARS -> new SkywarsGameManager(this.world, players, null, gameId, SkywarsGameManager.Settings.DEFAULT);
            case BEDWARS -> new BedwarsGameManager(this.world, players, new SpreadRules(4), gameId);
            case PROP_HUNT -> new PropHuntGameManager(this.world, players, null, gameId);
            case HIDE_AND_SEEK -> new HideAndSeekGameManager(this.world, players, null, gameId);
        };

        final boolean startedGame = this.startGame(game);
        if (!startedGame) SocWars.LOGGER.warn("Failed to start game {}", game.getGameId());

        return true;
    }

    public boolean completeQueue(GameType queueType) {
        return this.queue.finishQueue(queueType);
    }

    public boolean isPlayerInGame(ServerPlayerEntity player) {
        return this.playerGameLookup.get(player.getUuid()) != null;
    }

    public boolean allowsMultiQueue() {
        return this.queue.allowsMultiQueue();
    }

    public boolean allowSinglePlayer(GameType gameType, boolean allow) {
        return allow ? this.allowSinglePlayer(gameType) : this.disallowSinglePlayer(gameType);
    }

    public boolean allowSinglePlayer(GameType gameType) {
        return this.queue.allowSinglePlayer(gameType);
    }

    public boolean disallowSinglePlayer(GameType gameType) {
        this.queue.disallowSinglePlayer(gameType);
        return true;
    }
}
