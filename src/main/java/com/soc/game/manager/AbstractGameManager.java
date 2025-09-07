package com.soc.game.manager;

import com.soc.game.map.AbstractGameMap;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;

public abstract class AbstractGameManager<T extends AbstractGameManager.GameType> {
    public static class GameType {}

    protected final AbstractGameMap<T> map;
    protected final HashSet<PlayerEntity> players = new HashSet<>(8);

    protected AbstractGameManager(AbstractGameMap<T> map) {
        this.map = map;
    }

    public abstract void onPlayerDeath();
}
