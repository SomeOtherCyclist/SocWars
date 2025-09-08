package com.soc.game.map;

import com.google.common.collect.ImmutableSet;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Stack;

public class SkywarsGameMap extends AbstractGameMap {
    public SkywarsGameMap(
            StructureTemplate structure,
            ImmutableSet<BlockPos> spawnLocations,
            World world
    ) {
        super(structure, spawnLocations, world);
    }

    @Override
    public void spreadPlayers(ArrayList<ServerPlayerEntity> players) {
        final Stack<ServerPlayerEntity> playerStack = getRandomPlayerStack(players);

        spawnLocations.forEach((team, pos) -> {
            ServerPlayerEntity player = playerStack.pop();
            player.setPosition(pos.toCenterPos());
        });
    }

    @Override
    public void tick() {}

    @Override
    public ImmutableSet<Team> makeTeams() {
        return null;
    }
}
