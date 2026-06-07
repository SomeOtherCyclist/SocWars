package com.soc.game.manager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.soc.database.stats.SeekingTable;
import com.soc.game.map.AbstractHidingGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.lib.Events;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.game.map.AbstractHidingGameMap.*;
import static com.soc.lib.SocWarsLib.ifNotNull;

public abstract class AbstractHidingGameManager<MAP extends AbstractHidingGameMap, TABLE extends SeekingTable, EVENT extends AbstractHidingGameManager<?, ?, ?>> extends AbstractGameManager<MAP, TABLE, EVENT> {
	protected AbstractHidingGameManager(GameType gameType, ServerWorld world, Set<ServerPlayerEntity> players, SpreadRules spreadRules, int gameId) {
		super(gameType, world, players, spreadRules, gameId);
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

	protected void endGame(boolean immediate, DyeColor winningTeam) {
		this.getPlayers().forEach(player -> {
			final DyeColor playerTeam = this.getTeam(player);
			final String playerTeamSuffix = playerTeam == SEEKER_COLOUR ? "seeker" : "hider";

			final Text message;
			final SoundEvent sound;
			final SeekingTable dbTable = this.getDbTable(player);
			if (playerTeam == winningTeam) {
				message = Text.translatable("game.hiding.win." + playerTeamSuffix);
				sound = SoundEvents.ENTITY_PLAYER_LEVELUP;
				dbTable.win(this);
			} else {
				message = Text.translatable("game.hiding.lose." + playerTeamSuffix);
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

		if (this.getTeam(player) == SEEKER_COLOUR) {
			this.onSeekerDeath(player, source, amount);
		} else {
			this.onHiderDeath(player, source, amount);
		}

		return false;
	}

	protected abstract void onHiderDeath(ServerPlayerEntity player, DamageSource source, float amount);

	protected void onSeekerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
		ifNotNull(this.getDbTable(source.getSource()), SeekingTable::killSeeker);
		player.changeGameMode(GameMode.SPECTATOR);

		this.endGame(false, HIDER_COLOUR);
	}

	@Override
	@Nullable
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

	protected void taunt(ServerPlayerEntity player) {
		this.world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value(), SoundCategory.MASTER, 5, 1);
		player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 0, false, false));
	}

	public abstract void tryFindPlayer(LivingEntity seeker, ServerPlayerEntity hider);

	protected void findPlayer(LivingEntity seeker, ServerPlayerEntity hider) {
		hider.changeGameMode(GameMode.SPECTATOR);

		if (seeker != null) {
			hider.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(hider.getId(), hider.getPos().subtract(seeker.getPos()).normalize().multiply(2.5d)));
			hider.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.hiding.found", seeker.getDisplayName())));
		}
		this.getDbTable(hider).find();

		if (seeker instanceof ServerPlayerEntity seekerEntity) {
			seekerEntity.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.hiding.find", hider.getDisplayName())));
			this.getDbTable(seeker).findPlayer();
		}

		this.teams.remove(HIDER_COLOUR, hider.getUuid());
		this.teams.put(FOUND_COLOUR, hider.getUuid());

		if (this.getAlivePlayers().isEmpty()) {
			this.endGame(false, SEEKER_COLOUR);
		}
	}

	//Maybe refactor this so that each manager has a function to determine whether a player is 'in'
	private Collection<UUID> getAlivePlayers() {
		return this.teams.get(HIDER_COLOUR);
	}

	@Override
	protected void tickKillzone() {}
}
