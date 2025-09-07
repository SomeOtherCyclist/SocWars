package com.soc.game.map;

import com.soc.game.manager.AbstractGameManager;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public abstract class AbstractGameMap<T extends AbstractGameManager.GameType> {
    protected final StructureTemplate structure;
    protected final ArrayList<BlockPos> spawnLocations;

    protected AbstractGameMap(StructureTemplate structure, ArrayList<BlockPos> spawnLocations) {
        this.structure = structure;
        this.spawnLocations = spawnLocations;
    }
}
