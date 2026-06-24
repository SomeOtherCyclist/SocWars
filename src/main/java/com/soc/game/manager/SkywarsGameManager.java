package com.soc.game.manager;

import com.google.common.collect.Multimap;
import com.soc.SocWars;
import com.soc.database.stats.SkywarsTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.lib.Events;
import com.soc.networking.helper.SkywarsTeam;
import com.soc.networking.s2c.skywars.JoinSkywarsPayload;
import com.soc.networking.s2c.skywars.LeaveSkywarsPayload;
import com.soc.networking.s2c.skywars.SetTeamLivesPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.lib.SocWarsLib.*;

public class SkywarsGameManager extends AbstractGameManager<SkywarsGameMap, SkywarsTable, SkywarsGameManager> {
    private final Settings settings;
    private final Map<UUID, PlayerStats> playerMap;

    private final Map<UUID, CommandBossBar> bossBarMap;

    public static class Settings {
        public static final Settings DEFAULT = new Settings(5);

        private final int lives;

        public Settings(int lives) {
            this.lives = lives;
        }
    }

    private class PlayerStats {
        private int lives;

        private PlayerStats() {
            this.lives = SkywarsGameManager.this.settings.lives;
        }

        private int kill() {
            return --this.lives;
        }

        private boolean isAlive() {
            return this.lives > 0;
        }

        private int getLives() {
            return this.lives;
        }
    }

    protected SkywarsGameManager(
            ServerWorld world,
            Set<ServerPlayerEntity> players,
            @Nullable SpreadRules spreadRules,
            int gameId,
            Settings settings
    ) {
        super(GameType.SKYWARS, world, players, spreadRules, gameId);
        this.settings = settings;
        this.playerMap = players.stream().collect(Collectors.toMap(Entity::getUuid,key -> new PlayerStats()));
        this.bossBarMap = new HashMap<>(2, 1f);
    }

    @Override
    protected SkywarsGameMap buildMap() {
        final Optional<SkywarsGameMap> map = AbstractGameMap.loadRandomMap(super.world, super.generateCentrePosition(), SkywarsGameMap::fromNbt, SkywarsGameMap.FILE_EXTENSION);

        if (map.isEmpty()) throw new IllegalStateException("No Skywars map found");
        return map.get();
    }

    @Override
    public void startGame() {
        super.startGame();
        this.map.placeLootChests();

        this.onPlayerEliminate(null);
    }

    @Override
    public void endGame(boolean immediate) {
        this.setGameMode(GameMode.SPECTATOR);

        this.playerMap.forEach((uuid, stats) -> {
            final Text message;
            final SoundEvent sound;
            final SkywarsTable dbTable = this.getDbTable(uuid);
            if (stats.isAlive()) {
                message = Text.translatable("game.skywars.win");
                sound = SoundEvents.ENTITY_PLAYER_LEVELUP;
                dbTable.win();
            } else {
                message = Text.translatable("game.skywars.lose");
                sound = SoundEvents.BLOCK_BELL_USE;
                dbTable.lose();
            }

            Events.getInstance().scheduleEvent(() -> {
                if (this.world.getPlayerByUuid(uuid) instanceof ServerPlayerEntity player) {
                    player.networkHandler.sendPacket(new TitleS2CPacket(message));

                    player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1, 1);
                }
            }, 10);
        });

        for (CommandBossBar bar : this.bossBarMap.values()) {
            this.broadcastPacket(BossBarS2CPacket.remove(bar.getUuid()));
        }

        if (immediate) {
            super.endGame(true);
        } else {
            Events.getInstance().scheduleEvent(() -> super.endGame(false), 5 * 20);
        }
    }

    @Override
    public Multimap<DyeColor, UUID> buildTeams(Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules) {
        final Stack<UUID> playerStack = getRandomPlayerStack(players);

        final List<DyeColor> teamColoursList = new ArrayList<>(super.map.getTeamColours());
        Collections.shuffle(teamColoursList);

        return multimapFromCollections(teamColoursList, playerStack);
    }

    @Override
    protected EventQueue<SkywarsGameManager> buildEventQueue() {
        return super.buildEventQueue().addEvent(20 * 60 * 20, manager -> manager.endGame(false), Text.translatable("events.game.end"));
    }

    @Override
    protected Function<UUID, SkywarsTable> dbTableBuilder() {
        return SkywarsTable::new;
    }

    @Override
    protected void sendJoinGamePayload(ServerPlayerEntity player) {
        super.sendJoinGamePayload(player);
        ServerPlayNetworking.send(player, new JoinSkywarsPayload(this.getGameId(), this.playerMap.entrySet().stream().collect(Collectors.toMap(entry -> this.getTeam(entry.getKey()), entry -> new SkywarsTeam(entry.getKey(), entry.getValue().lives)))));
    }

    @Override
    protected void sendLeaveGamePayload(ServerPlayerEntity player) {
        super.sendLeaveGamePayload(player);
        ServerPlayNetworking.send(player, new LeaveSkywarsPayload());
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        if (player.isSpectator()) return false;

        super.onPlayerDeath(player, source, amount);

        final int livesRemaining = this.playerMap.get(player.getUuid()).kill();

        final boolean canRespawn = this.canRespawn(player);
        this.broadcastDeath(player, source, !canRespawn);
        this.broadcastPacket(new SetTeamLivesPayload(this.getTeam(player), livesRemaining));

        if (this.getAlivePlayers().size() < (super.getPlayers().size() > 1 ? 2 : 1)) {
            this.endGame(false);
            return false;
        }

        if (canRespawn) {
            PrescheduledEvents.playCountdown(() -> this.respawnPlayer(player), this, 3, 20, SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value(), true, player);
        } else {
            this.onPlayerEliminate(player);
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.skywars.eliminate")));
        }

        return false;
    }

    @Override
    protected void respawnPlayer(ServerPlayerEntity player) {
        super.respawnPlayer(player);
        player.giveItemStack(new ItemStack(woolItemFromColour(this.getTeam(player.getUuid())), 32));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 3 * 20, 4, false, false));
    }

    @Override
    @Nullable
    public Entity getWinningPlayer(@Nullable Entity except) {
        if (this.playerMap.size() == 1) return except;

        final Stream<Map.Entry<UUID, PlayerStats>> playersStream = this.playerMap
                .entrySet()
                .stream();
        final Stream<Map.Entry<UUID, PlayerStats>> playersStreamFiltered = except == null ? playersStream : playersStream.filter(entry -> entry.getKey() != except.getUuid());
        final OptionalInt mostLivesRemainingOptional = playersStreamFiltered
                .mapToInt(entry -> entry.getValue().getLives())
                .max();

        if (mostLivesRemainingOptional.isEmpty()) return null;
        final int mostLivesRemaining = mostLivesRemainingOptional.getAsInt();

        final Stream<UUID> playersWithMostLives = this.playerMap.entrySet().stream().filter(entry -> entry.getValue().getLives() == mostLivesRemaining).map(Map.Entry::getKey);
        return except == null ?
                playersWithMostLives
                        .min(Comparator.comparingInt(uuid -> this.world.random.nextInt()))
                        .map(this.world::getPlayerByUuid)
                        .orElse(null) :
                playersWithMostLives
                        .filter(player -> player != except.getUuid())
                        .map(this.world::getPlayerByUuid)
                        .min(Comparator.comparingDouble(player -> player.distanceTo(except)))
                        .orElse(null);
    }

    @Override
    protected boolean canRespawn(ServerPlayerEntity player) {
        return this.playerMap.get(player.getUuid()).isAlive();
    }

    @Override
    protected void trackDeathStats(ServerPlayerEntity player, DamageSource source) {
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) this.getDbTable(player).fallInVoid();

        final SkywarsTable targetTable = this.getDbTable(player);

        final boolean isFinal = !this.canRespawn(player);

        if (isFinal) {
            targetTable.eliminate();
        } else {
            targetTable.grantDeath();
        }

        getPlayerAttacker(player).ifPresent(killer -> {
            final SkywarsTable killerTable = this.getDbTable(killer);
            if (killerTable == null) return;

            if (isFinal) {
                targetTable.grantElimination();
            } else {
                targetTable.grantKill();
            }
        });
    }

    private Identifier getBossBarId() {
        return Identifier.of(SocWars.MOD_ID, "skywars_boss_bar_game_" + this.gameId);
    }

    private void onPlayerEliminate(ServerPlayerEntity player) { //Redo this all properly
        final List<UUID> alivePlayers = this.getAlivePlayers();
        if (alivePlayers.size() == 2) {
            final List<ServerPlayerEntity> serverPlayerEntities = mapUuidsToPlayers(this.world, alivePlayers);
            if (serverPlayerEntities.size() != 2) return;

            this.bossBarMap.put(serverPlayerEntities.get(1).getUuid(), new CommandBossBar(this.getBossBarId(), serverPlayerEntities.get(1).getDisplayName()));
            this.bossBarMap.put(serverPlayerEntities.get(0).getUuid(), new CommandBossBar(this.getBossBarId(), serverPlayerEntities.get(0).getDisplayName()));

            serverPlayerEntities.get(0).networkHandler.sendPacket(BossBarS2CPacket.add(this.bossBarMap.get(serverPlayerEntities.get(1).getUuid())));
            serverPlayerEntities.get(1).networkHandler.sendPacket(BossBarS2CPacket.add(this.bossBarMap.get(serverPlayerEntities.get(0).getUuid())));

            this.broadcastSound(SoundEvents.ENTITY_WITHER_SPAWN);

            //this.onPlayerDamage(serverPlayerEntities.get(0), null, 0f);
            //this.onPlayerDamage(serverPlayerEntities.get(1), null, 0f); //More disgusting code
        }
    }

    @Override
    public boolean onChestOpened(ServerPlayerEntity player, BlockPos pos) {
        if (player.isSpectator()) return true;
        this.map.getLootChest(pos).ifPresent(chest -> {
            if (chest.open(player)) {
                this.map.populateInventory(chest.getTier(), pos, chest.getFillOrdinal());
                this.getDbTable(player).openChest(chest.getTier());
            }
        });

        return true;
    }

    //@Override
    //public boolean onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
    //    if (!this.bossBarMap.isEmpty()) {
    //        this.bossBarMap.entrySet().stream().filter(entry -> !entry.getKey().equals(player.getUuid())).findFirst().ifPresent(entry -> {
    //            ifNotNull(this.world.getPlayerByUuid(entry.getKey()), otherPlayer -> {
    //                entry.getValue().setMaxValue((int)player.getMaxHealth());
    //                entry.getValue().setValue((int)player.getHealth());
    //                ((ServerPlayerEntity)otherPlayer).networkHandler.sendPacket(BossBarS2CPacket.updateProgress(entry.getValue()));
    //            });
    //        });
    //    }

    //    return super.onPlayerDamage(player, source, amount);
    //}

    private List<UUID> getAlivePlayers() {
        return this.playerMap.entrySet().stream().filter(entry -> entry.getValue().isAlive()).map(Map.Entry::getKey).toList();
    }
}
