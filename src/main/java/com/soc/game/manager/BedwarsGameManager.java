package com.soc.game.manager;

import com.soc.game.map.AbstractGameMap;

public class BedwarsGameManager extends AbstractGameManager<BedwarsGameManager.BedWarsGameType> {
    public static class BedWarsGameType extends AbstractGameManager.GameType {}

    protected BedwarsGameManager(AbstractGameMap<BedWarsGameType> map) {
        super(map);
    }

    @Override
    public void onPlayerDeath() {

    }
}
