package com.soc.game.map;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

public class SkywarsGameMap extends AbstractGameMap {
    public SkywarsGameMap(
            StructureTemplate structure,
            Set<BlockPos> spawnLocations,
            BlockPos centrePos,
            Multimap<Team, ServerPlayerEntity> teams,
            World world
    ) {
        super(structure, spawnLocations, centrePos, teams, world);
    }

    @Override
    public void spreadPlayers(Multimap<Team, ServerPlayerEntity> teams) {

    }

    @Override
    public void tick() {}
}