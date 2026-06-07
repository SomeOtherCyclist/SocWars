package com.soc.game.manager;

import com.google.common.collect.Multimap;
import com.soc.lib.Events;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface PowerupGame {
	default boolean storePowerup(ServerPlayerEntity player, Powerup powerup) {
		if (this.playerHasPowerup(player, powerup)) {
			return false;
		} else {
			this.getPowerupMap().put(player, powerup.getId());
			return true;
		}
	}

	default boolean storePowerup(ServerPlayerEntity player, Powerup powerup, int duration) {
		if (this.playerHasPowerup(player, powerup)) {
			return false;
		} else {
			this.getPowerupMap().put(player, powerup.getId());
			Events.getInstance().scheduleEvent(() -> this.getPowerupMap().remove(player, powerup.getId()), duration);
			return true;
		}
	}

	default void removePowerup(ServerPlayerEntity player, Powerup powerup) {
		this.getPowerupMap().remove(player, powerup.getId());
	}

	default boolean playerHasPowerup(ServerPlayerEntity player, Powerup powerup) {
		return this.getPowerupMap().containsEntry(player, powerup.getId());
	}

	Multimap<ServerPlayerEntity, Identifier> getPowerupMap();
}
