package com.soc.game.map;

import com.soc.game.manager.BedwarsGameManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;

public class BedwarsGameMap extends AbstractGameMap<BedwarsGameManager.BedWarsGameType> {
    private final ArrayList<BedPos> bedPositions;
    private final HashMap<Team, BlockPos> islandGens;
    private final ArrayList<BlockPos> diamondGens;
    private final ArrayList<BlockPos> emeraldGens;

    public BedwarsGameMap(
            StructureTemplate structure,
            ArrayList<BlockPos> spawnLocations,
            ArrayList<BedPos> bedPositions,
            HashMap<Team, BlockPos> islandGens,
            ArrayList<BlockPos> diamondGens,
            ArrayList<BlockPos> emeraldGens
    ) {
        super(structure, spawnLocations);
        this.bedPositions = bedPositions;
        this.islandGens = islandGens;
        this.diamondGens = diamondGens;
        this.emeraldGens = emeraldGens;
    }
}
