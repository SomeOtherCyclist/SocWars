package com.soc.game.manager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.soc.database.stats.DuelsTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.DuelsGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.lib.Events;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class DuelsGameManager extends AbstractGameManager<DuelsGameMap, DuelsTable, DuelsGameManager> {
	protected DuelsGameManager(ServerWorld world, Set<ServerPlayerEntity> players, SpreadRules spreadRules, int gameId) {
		super(GameType.DUELS, world, players, spreadRules, gameId);
	}

	@Override
	protected DuelsGameMap buildMap() {
		final Optional<DuelsGameMap> map = AbstractGameMap.loadRandomMap(this.world, this.generateCentrePosition(), DuelsGameMap::fromNbt, DuelsGameMap.FILE_EXTENSION);

		if (map.isEmpty()) throw new IllegalStateException("No Duels map found");

		return map.get();
	}

	@Override
	public void startGame() {
		super.startGame();
		this.map.placeLootChests();
	}

	@Override
	public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
		this.endGame(false, player);
		return false;
	}

	public void endGame(boolean immediate, ServerPlayerEntity dyingPlayer) {
		this.playersForEach((team, player2) -> {
			final Text message;
			final SoundEvent sound;
			final DuelsTable dbTable = this.getDbTable(player2);
			if (dyingPlayer != player2) {
				message = Text.translatable("game.bedwars.win");
				sound = SoundEvents.ENTITY_PLAYER_LEVELUP;
				dbTable.win();
			} else {
				message = Text.translatable("game.bedwars.lose");
				sound = SoundEvents.BLOCK_BELL_USE;
				dbTable.lose();
			}

			Events.getInstance().scheduleEvent(() -> {
				player2.networkHandler.sendPacket(new TitleS2CPacket(message));

				player2.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1, 1);
			}, 10);
		});
		super.endGame(immediate);
	}

	@Override
	protected Multimap<DyeColor, UUID> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules) {
		final Iterator<DyeColor> colours = this.map.getTeamColours().iterator();

		return players.stream().collect(Multimaps.toMultimap(key -> colours.next(), ServerPlayerEntity::getUuid, HashMultimap::create));
	}

	@Override
	protected Function<UUID, DuelsTable> dbTableBuilder() {
		return DuelsTable::new;
	}

	@Override
	public @Nullable Entity getWinningPlayer(@Nullable Entity except) {
		return this.getPlayers().stream().filter(player -> player != except).max(Comparator.comparingDouble(ServerPlayerEntity::getHealth)).orElse(null);
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

	@Override
	protected boolean hasTeamChat() {
		return false;
	}
}
