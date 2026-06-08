package com.soc.events;

import net.minecraft.server.network.ServerPlayerEntity;

public interface AfterPlayerLeave {
	void afterLeave(ServerPlayerEntity player);
}
