package com.soc.game.map;

import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SpawnPosition;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;

public class PropHuntGameMap extends HideAndSeekGameMap {
	private final Set<Block> allowedMorphs;

	public PropHuntGameMap(StructureTemplate structure, Set<SpawnPosition> spawnPositions, BlockPos centrePos, BlockPos absoluteCentrePos, @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay, int minBuildY, int maxBuildY, ServerWorld world, File file) {
		super(structure, spawnPositions, centrePos, absoluteCentrePos, blockProtectionOverlay, minBuildY, maxBuildY, world, file);
		this.allowedMorphs = Set.of();
	}

	public boolean allowsMorph(BlockState morph) {
		return this.allowedMorphs.contains(morph.getBlock());
	}
}
