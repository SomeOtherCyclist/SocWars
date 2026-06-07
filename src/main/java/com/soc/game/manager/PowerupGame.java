package com.soc.game.manager;

import com.google.common.collect.Multimap;
import com.soc.lib.Events;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PowerupGame {
	default boolean storePowerup(ServerPlayerEntity player, Powerup powerup) {
		if (this.playerHasPowerup(player, powerup)) {
			return false;
		} else {
			this.getPowerupMap().put(player, powerup);
			return true;
		}
	}

	default boolean storePowerup(ServerPlayerEntity player, Powerup powerup, int duration) {
		if (this.playerHasPowerup(player, powerup)) {
			return false;
		} else {
			this.getPowerupMap().put(player, powerup);
			Events.getInstance().scheduleEvent(() -> this.getPowerupMap().remove(player, powerup), duration);
			return true;
		}
	}

	default boolean playerHasPowerup(ServerPlayerEntity player, Powerup powerup) {
		return this.getPowerupMap().containsEntry(player, powerup);
	}

	Multimap<ServerPlayerEntity, Powerup> getPowerupMap();
}
