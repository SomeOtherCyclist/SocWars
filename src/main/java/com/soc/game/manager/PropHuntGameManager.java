package com.soc.game.manager;

import com.soc.game.map.PropHuntGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class PropHuntGameManager extends HideAndSeekGameManager {
	protected PropHuntGameManager(ServerWorld world, Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules, int gameId) {
		super(world, players, spreadRules, gameId);
	}

	@Override
	public boolean onPlayerMorphed(ServerPlayerEntity player, BlockState morph) {
		return this.getMap().allowsMorph(morph);
	}

	@Override
	protected PropHuntGameMap buildMap() {
		return null;
	}

	private PropHuntGameMap getMap() {
		return (PropHuntGameMap)this.map;
	}

	@Override
	public void endGame(boolean immediate) {
		this.removePlayersMorphs();
		super.endGame(immediate);
	}
}
