package com.soc.game.manager;

import com.soc.game.map.AbstractGameMap;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;

public abstract class AbstractGameManager {
    protected final AbstractGameMap map;
    protected final HashSet<PlayerEntity> players = new HashSet<>(8);

    protected AbstractGameManager(AbstractGameMap map) {
        this.map = map;
    }

    protected abstract AbstractGameMap getMap();
    public abstract void onPlayerDeath();
}
