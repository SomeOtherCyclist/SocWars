package com.soc.game.map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public class SkywarsGameMap extends AbstractGameMap {
    public static final String FILE_EXTENSION = "swmap";

    public SkywarsGameMap(
            StructureTemplate structure,
            ImmutableMap<DyeColor, BlockPos> spawnPositions,
            BlockPos centrePos,
            ServerWorld world
    ) {
        super(structure, spawnPositions, centrePos, world);
    }

    @Override
    public void tick() {}
}