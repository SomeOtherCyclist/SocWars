package com.soc.game.map;

import com.soc.game.manager.BedwarsGameManager;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class BedwarsGameMap extends AbstractGameMap<BedwarsGameManager.BedWarsGameType> {
    private final ArrayList<BedPos> bedPositions;

    public BedwarsGameMap(StructureTemplate structure, ArrayList<BlockPos> spawnLocations, ArrayList<BedPos> bedPositions) {
        super(structure, spawnLocations);
        this.bedPositions = bedPositions;
    }
}
