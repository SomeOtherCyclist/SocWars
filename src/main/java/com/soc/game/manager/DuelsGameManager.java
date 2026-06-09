package com.soc.game.manager;

import com.google.common.collect.Multimap;
import com.soc.database.stats.DuelsTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.BedwarsGameMap;
import com.soc.game.map.DuelsGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
	protected Multimap<DyeColor, UUID> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules) {
		return null;
	}

	@Override
	protected Function<UUID, DuelsTable> dbTableBuilder() {
		return DuelsTable::new;
	}

	@Override
	public @Nullable Entity getWinningPlayer(@Nullable Entity except) {
		return null;
	}
}
