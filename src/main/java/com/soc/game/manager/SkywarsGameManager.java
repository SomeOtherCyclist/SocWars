package com.soc.game.manager;

import com.soc.game.map.SkywarsGameMap;

public class SkywarsGameManager extends AbstractGameManager {
    protected SkywarsGameManager(SkywarsGameMap map) {
        super(map);
    }

    @Override
    protected SkywarsGameMap getMap() {
        return (SkywarsGameMap) super.map;
    }

    @Override
    public void onPlayerDeath() {

    }
}
