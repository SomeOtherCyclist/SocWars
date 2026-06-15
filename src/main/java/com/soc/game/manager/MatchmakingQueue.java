package com.soc.game.manager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.soc.SocWars;
import com.soc.events.ModEvents;
import com.soc.networking.helper.QueueProgress;
import com.soc.networking.s2c.QueueProgressPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public class MatchmakingQueue {
    private final World world;

    private final boolean allowMultiQueue = false;

    private final Multimap<GameType, UUID> queue;
    private final HashMap<GameType, Long> queueCountdowns;
    private final BiConsumer<GameType, Set<ServerPlayerEntity>> queueCompletionFunction;
    private final Set<GameType> allowedSinglePlayerQueues;

    private boolean dirty = false;

    public MatchmakingQueue(World world, BiConsumer<GameType, Set<ServerPlayerEntity>> queueCompletionFunction) {
		this.world = world;
		this.queue = HashMultimap.create();
        this.queueCountdowns = new HashMap<>();
		this.queueCompletionFunction = queueCompletionFunction;
        this.allowedSinglePlayerQueues = new HashSet<>(GameType.values().length);

        ModEvents.AFTER_PLAYER_LEAVE.register(player -> {
            if (this.isPlayerInQueue(player)) {
                this.checkCountdownsOnLeave();
                this.markDirty();
            }
        });

        ServerPlayerEvents.JOIN.register(player -> {
            if (this.isPlayerInQueue(player)) {
                this.checkCountdownsOnJoin(player);
                this.markDirty();
            }
        });
    }

    public void queuePlayer(ServerPlayerEntity player, GameType gameType) {
        if (this.isPlayerInQueue(player, gameType)) return;

        if (!this.allowMultiQueue) this.unqueuePlayer(player);
        this.queue.put(gameType, player.getUuid());

        if (!this.queueCountdowns.containsKey(gameType)) {
            this.startCountdown(player, gameType);
        }

        this.markDirty();
    }

    private void startCountdown(ServerPlayerEntity player, GameType gameType) {
        this.queueCountdowns.put(gameType, this.world.getTime() + 30 * 20);

        final Text text = Text.translatable("message.queue.queue_started", Objects.requireNonNull((MutableText)player.getDisplayName()).formatted(Formatting.GREEN), gameType.getVariantName().formatted(Formatting.GOLD));
        for (ServerPlayerEntity playerNotInGame : GamesManager.getInstance().getPlayersNotInGame()) {
            playerNotInGame.sendMessage(text, false);
        }
    }

    private void checkCountdownsOnLeave() {
        SocWars.LOGGER.info("player left");

        for (GameType gameType : this.queueCountdowns.keySet()) {
            SocWars.LOGGER.info("checking {}", gameType.asString());

            if (!this.queueHasPlayers(gameType)) {
                SocWars.LOGGER.info("{} is empty, removing countdown", gameType.asString());

                this.queueCountdowns.remove(gameType);
            }
        }
    }

    private void checkCountdownsOnJoin(ServerPlayerEntity player) {
        for (GameType gameType : this.getPlayerQueues(player)) {
            if (!this.queueCountdowns.containsKey(gameType)) this.startCountdown(player, gameType);
        }
    }

    public void unqueuePlayer(ServerPlayerEntity player, GameType gameType) {
        this.queue.remove(gameType, player.getUuid());
        if (!this.queueHasPlayers(gameType)) {
            this.queueCountdowns.remove(gameType);
            this.allowedSinglePlayerQueues.remove(gameType);
        }

        this.markDirty();
    }

    public void unqueuePlayer(ServerPlayerEntity player) {
        for (GameType gameType : GameType.values()) {
            this.unqueuePlayer(player, gameType);
        }
    }

    public void unqueuePlayers(Collection<ServerPlayerEntity> players, GameType gameType) {
        for (ServerPlayerEntity player : players) {
            this.unqueuePlayer(player, gameType);
        }
    }

    public void unqueuePlayers(Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            this.unqueuePlayer(player);
        }
    }

    public void setPlayerQueues(ServerPlayerEntity player, Collection<GameType> gameTypes) {
        for (GameType gameType : GameType.values()) {
            if (gameTypes.contains(gameType)) {
                this.queuePlayer(player, gameType);
            } else {
                this.unqueuePlayer(player, gameType);
            }
        }
    }

    public boolean isPlayerInQueue(ServerPlayerEntity player, GameType gameType) {
        return this.queue.containsEntry(gameType, player.getUuid());
    }

    public boolean isPlayerInQueue(ServerPlayerEntity player) {
        return this.queue.containsValue(player.getUuid());
    }

    public Collection<GameType> getPlayerQueues(ServerPlayerEntity player) {
        return this.queue.asMap().entrySet().stream().filter(entry -> entry.getValue().contains(player.getUuid())).map(Map.Entry::getKey).toList();
    }

    public void tick() {
        for (GameType gameType : GameType.values()) {
            final Set<ServerPlayerEntity> players = this.getLimitedPlayers(gameType);

            ifNotNull(this.queueCountdowns.get(gameType), time -> {
                if (this.world.getTime() > time) {
                    if (players.size() >= gameType.minPlayers() || this.allowedSinglePlayerQueues.contains(gameType)) {
                        this.finishQueue(gameType, players);
                    } else {
                        for (ServerPlayerEntity player : players) {
                            player.sendMessage(Text.translatable("message.queue.queue_failed", gameType.getVariantName()), false);
                        }
                        this.unqueuePlayers(players, gameType);
                    }
                }
            });
        }

        if (this.dirty) {
            this.sendQueueProgress();
            this.dirty = false;
        }
    }

    private void finishQueue(GameType gameType, Set<ServerPlayerEntity> players) {
        this.allowedSinglePlayerQueues.remove(gameType);
        this.queueCompletionFunction.accept(gameType, players);

        this.unqueuePlayers(players);
    }

    public boolean finishQueue(GameType gameType) {
        final Set<ServerPlayerEntity> players = this.getLimitedPlayers(gameType);
        if (players.isEmpty()) {
            return false;
        } else {
            this.finishQueue(gameType, players);
            return true;
        }
    }

    public Set<ServerPlayerEntity> getLimitedPlayers(GameType gameType) {
        return this.queue.get(gameType).stream().map(uuid -> (ServerPlayerEntity)this.world.getPlayerByUuid(uuid)).filter(Objects::nonNull).limit(gameType.maxPlayers()).collect(Collectors.toSet());
    }

    public boolean allowsMultiQueue() {
        return this.allowMultiQueue;
    }

    private void markDirty() {
        this.dirty = true;
    }

    public void sendQueueProgress() {
        final QueueProgressPayload payload = this.getQueueProgressPayload();

        for (ServerPlayerEntity player : GamesManager.getInstance().getPlayersNotInGame()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public QueueProgressPayload getQueueProgressPayload() {
		return new QueueProgressPayload(this.queueCountdowns.keySet().stream().collect(Collectors.toMap(Function.identity(), gameType -> new QueueProgress(this.getPlayersInQueue(gameType).size(), this.queueCountdowns.get(gameType), this.allowedSinglePlayerQueues.contains(gameType)))));
    }

    private List<UUID> getPlayersInQueue(GameType gameType) {
        return this.queue.get(gameType).stream().filter(this::playerIsLoggedIn).toList();
    }

    private boolean queueHasPlayers(GameType gameType) {
        return this.queue.get(gameType).stream().anyMatch(this::playerIsLoggedIn);
    }

    private boolean playerIsLoggedIn(UUID uuid) {
        return this.world.getPlayerByUuid(uuid) != null;
    }

    public boolean allowSinglePlayer(GameType gameType) {
        if (this.queue.get(gameType).isEmpty()) return false;

        this.allowedSinglePlayerQueues.add(gameType);
        this.markDirty();

        return true;
    }

    public void disallowSinglePlayer(GameType gameType) {
        this.allowedSinglePlayerQueues.remove(gameType);
        this.markDirty();
    }
}
