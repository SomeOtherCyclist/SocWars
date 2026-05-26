package com.soc.game.map;

import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SpawnPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public abstract class AbstractHidingGameMap extends AbstractGameMap {
	public static final DyeColor SEEKER_COLOUR = DyeColor.RED;
	public static final DyeColor HIDER_COLOUR = DyeColor.BLUE;
	public static final DyeColor FOUND_COLOUR = DyeColor.GRAY;

	public AbstractHidingGameMap(
			StructureTemplate structure,
			Set<SpawnPosition> spawnPositions,
			BlockPos centrePos,
			BlockPos absoluteCentrePos,
			@Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
			int minBuildY,
			int maxBuildY,
			ServerWorld world,
			File file
	) {
		super(structure, spawnPositions, centrePos, absoluteCentrePos, blockProtectionOverlay, minBuildY, maxBuildY, world, file);
	}

	/// Constructor used only for saving the map to file
	public AbstractHidingGameMap(
			StructureTemplate structure,
			Set<SpawnPosition> spawnPositions,
			BlockPos centrePos,
			@Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
			Map<String, Integer> fields
	) {
		super(structure, spawnPositions, centrePos, blockProtectionOverlay);
	}

	@Override
	public Optional<BlockPos> getSpawnPosition(DyeColor team) {
		return team == SEEKER_COLOUR ? super.getSpawnPosition(team).map(pos -> pos.withY(this.world.getTopYInclusive() - 4)) : super.getSpawnPosition(team);
	}

	public Optional<BlockPos> getSpawnPositionNoOffset(DyeColor team) {
		return super.getSpawnPosition(team);
	}

	@Override
	public Collection<BlockPos> getSpawnPositions(DyeColor team) {
		return team == SEEKER_COLOUR ? super.getSpawnPositions(team).stream().map(pos -> pos.withY(this.world.getTopYInclusive() - 4)).toList() : super.getSpawnPositions(team);
	}

	@Override
	public NbtCompound toNbt(NbtCompound compound) {
		super.toNbt(compound);

		return compound;
	}

	@Override
	public void tick() {}
}