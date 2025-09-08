package com.soc.game.manager;

import com.soc.SocWars;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.BedwarsGameMap;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class BedwarsGameManager extends AbstractGameManager {
    protected BedwarsGameManager(BedwarsGameMap map) {
        super(map);
    }

    @Override
    protected BedwarsGameMap getMap() {
        return (BedwarsGameMap) super.map;
    }

    @Override
    public void onPlayerDeath() {

    }
}
