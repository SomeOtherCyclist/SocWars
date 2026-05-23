package com.soc.game.manager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.soc.database.stats.BaseTable;
import com.soc.database.stats.HideAndSeekTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.HideAndSeekGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.items.AttackFunctionWeapon;
import com.soc.lib.Events;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.game.map.HideAndSeekGameMap.*;
import static com.soc.lib.SocWarsLib.scaleEntity;

public class HideAndSeekGameManager extends AbstractGameManager<HideAndSeekGameMap, HideAndSeekTable, HideAndSeekGameManager> {
    protected HideAndSeekGameManager(
            ServerWorld world,
            Set<ServerPlayerEntity> players,
            @Nullable SpreadRules spreadRules,
            int gameId
    ) {
        super(GameType.HIDE_AND_SEEK, world, players, spreadRules, gameId);
    }

    @Override
    protected HideAndSeekGameMap buildMap() {
        final Optional<HideAndSeekGameMap> map = AbstractGameMap.loadRandomMap(super.world, super.generateCentrePosition(), HideAndSeekGameMap::fromNbt, HideAndSeekGameMap.FILE_EXTENSION);

        if (map.isEmpty()) throw new IllegalStateException("No Hide and Seek map found");
        return map.get();
    }

    @Override
    protected void onFinishCountdown() {
        this.map.spawnCages(false, HIDER_COLOUR);
        this.getPlayers(HIDER_COLOUR).forEach(hider -> {
            hider.changeGameMode(GameMode.SURVIVAL);
            scaleEntity(hider, 0.75f);
        });

        final TitleS2CPacket youAreSeekingPacket = new TitleS2CPacket(Text.translatable("game.hide_and_seek.you_are_seeking"));
        this.getPlayers(SEEKER_COLOUR).forEach(player -> {
            player.giveItemStack(new ItemStack(AttackFunctionWeapon.SEEKING_STICK));
            player.networkHandler.sendPacket(youAreSeekingPacket);
        });

        PrescheduledEvents.playCountdown(() -> {
            this.map.spawnCages(false, SEEKER_COLOUR);
            this.getPlayers(SEEKER_COLOUR).forEach(seeker -> {
                seeker.changeGameMode(GameMode.SURVIVAL);

                final Optional<Vec3d> seekerSpawn = this.map.getSpawnPositionNoOffset(SEEKER_COLOUR).map(BlockPos::toCenterPos);
                seekerSpawn.ifPresent(pos -> seeker.requestTeleport(pos.x, pos.y, pos.z));
                seeker.fallDistance = 0d;
            });
        }, this, 15, 20, SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(), true);
    }

    @Override
    public void startGame() {
        super.startGame();
        this.getPlayers(SEEKER_COLOUR).forEach(player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 450, 0, false, false)));
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public void endGame(boolean immediate) {
        this.endGame(immediate, SEEKER_COLOUR);
    }

    private void endGame(boolean immediate, DyeColor winningTeam) {
        this.getPlayers().forEach(player -> {
            final DyeColor playerTeam = this.getTeam(player);
            final String playerTeamSuffix = playerTeam == SEEKER_COLOUR ? "seeker" : "hider";

            final Text message;
            final SoundEvent sound;
            final HideAndSeekTable dbTable = this.getDbTable(player);
            if (playerTeam == winningTeam) {
                message = Text.translatable("game.hide_and_seek.win." + playerTeamSuffix);
                sound = SoundEvents.ENTITY_PLAYER_LEVELUP;
                dbTable.win();
            } else {
                message = Text.translatable("game.hide_and_seek.lose." + playerTeamSuffix);
                sound = SoundEvents.BLOCK_BELL_USE;
                dbTable.lose();
            }

            Events.getInstance().scheduleEvent(() -> {
                player.networkHandler.sendPacket(new TitleS2CPacket(message));

                player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1, 1);
            }, 10);
        });

        if (immediate) {
            super.endGame(true);
        } else {
            Events.getInstance().scheduleEvent(() -> super.endGame(false), 5 * 20);
        }
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        if (player.isSpectator()) return false;

        healPlayer(player);
        this.map.getSpawnPosition(this.getTeam(player.getUuid())).ifPresent(pos -> player.requestTeleport(pos.getX(), pos.getY(), pos.getZ()));

        if (this.getTeam(player) == SEEKER_COLOUR) {
            this.endGame(false, HIDER_COLOUR);
        }

        return false;
    }

    @Override
    public Entity getWinningPlayer(@Nullable Entity except) {
        return null;
    }

    @Override
    public Multimap<DyeColor, UUID> buildTeams(Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules) {
        final Stack<UUID> playerStack = getRandomPlayerStack(players);

        final HashMultimap<DyeColor, UUID> map = HashMultimap.create();

        map.put(SEEKER_COLOUR, playerStack.pop());

        while (!playerStack.isEmpty()) {
            map.put(HIDER_COLOUR, playerStack.pop());
        }

        return map;
    }

    @Override
    protected Map<DyeColor, Team> buildScoreboardTeams() {
        final Map<DyeColor, Team> teams = super.buildScoreboardTeams();
        teams.put(FOUND_COLOUR, this.addTeamFromColour(FOUND_COLOUR));

        return teams;
    }

    @Override
    protected Function<UUID, HideAndSeekTable> dbTableBuilder() {
        return HideAndSeekTable::new;
    }

    @Override
    protected EventQueue<HideAndSeekGameManager> buildEventQueue() {
        final EventQueue<HideAndSeekGameManager> hideAndSeekGameManagerEventQueue = super.buildEventQueue();

        for (int i = 1; i < 5; i++) {
            hideAndSeekGameManagerEventQueue.addEvent(i * 60 * 20, manager -> manager.getPlayers(HIDER_COLOUR).forEach(player -> {
                this.world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value(), SoundCategory.MASTER, 5, 1);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 0, false, false));
            }), Text.translatable("events.hide_and_seek.ping." + i));
        }
        hideAndSeekGameManagerEventQueue.addEvent(5 * 60 * 20, manager -> manager.endGame(false, HIDER_COLOUR), Text.translatable("events.hide_and_seek.end"));

        return hideAndSeekGameManagerEventQueue;
    }

    public void findPlayer(LivingEntity seeker, ServerPlayerEntity hider) {
        hider.changeGameMode(GameMode.SPECTATOR);
        hider.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(hider.getId(), hider.getPos().subtract(seeker.getPos()).normalize().multiply(2.5d)));
        hider.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.hide_and_seek.found", seeker.getDisplayName())));
        this.getDbTable(hider).grantFound();

        if (seeker instanceof ServerPlayerEntity seekerEntity) {
            seekerEntity.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.hide_and_seek.find", hider.getDisplayName())));
            this.getDbTable(seeker).grantFind();
        }

        super.teams.remove(HIDER_COLOUR, hider.getUuid());
        super.teams.put(FOUND_COLOUR, hider.getUuid());

        if (this.getAlivePlayers().isEmpty()) {
            this.endGame(false, SEEKER_COLOUR);
        }
    }

    //Maybe refactor this so that each manager has a function to determine whether a player is 'in'
    private Collection<UUID> getAlivePlayers() {
        return this.teams.get(HIDER_COLOUR);
    }
}
