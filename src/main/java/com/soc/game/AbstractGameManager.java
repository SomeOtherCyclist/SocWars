package com.soc.game;

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;

public abstract class AbstractGameManager {
    protected final HashSet<PlayerEntity> players = new HashSet<>(8);

    public abstract void onPlayerDeath();
}
