package com.soc.game.map;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class SkywarsGameMap extends AbstractGameMap {
    public static final String FILE_EXTENSION = "swmap";

    public SkywarsGameMap(
            StructureTemplate structure,
            @NotNull ImmutableMap<DyeColor, BlockPos> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull BlockPos absoluteCentrePos,
            @NotNull ServerWorld world
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, world);
    }

    /// Constructor used only for saving the map to file
    public SkywarsGameMap(
            StructureTemplate structure,
            @NotNull ImmutableMap<DyeColor, BlockPos> spawnPositions,
            @NotNull BlockPos centrePos
    ) {
        super(structure, spawnPositions, centrePos);
    }

    @Override
    public void tick() {}
}