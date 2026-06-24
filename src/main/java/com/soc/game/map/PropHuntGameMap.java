package com.soc.game.map;

import com.soc.SocWars;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SpawnPosition;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.soc.player.Morph.calculateBlockVolume;

public class PropHuntGameMap extends AbstractHidingGameMap {
	public static final String FILE_EXTENSION = "phmap";
	public static final String MIN_BLOCK_SIZE_KEY = "min_block_size";
	private static final int DEFAULT_GAME_DURATION = 5 * 60 * 20;
	public static final Map<String, RangedIntField> MAP_FIELDS = buildFields(
			new RangedIntField("game_duration", 0, 24 * 60 * 60, AbstractGameMap::setGameDurationSeconds),
			new RangedIntField("min_block_size", 0, 100, PropHuntGameMap::setMinBlockSize)
	);

	private final Set<Block> disallowedMorphs;
	private float minBlockSize = 0f;

	public PropHuntGameMap(
			StructureTemplate structure,
			Set<SpawnPosition> spawnPositions,
			BlockPos centrePos,
			BlockPos absoluteCentrePos,
			@Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
			int minBuildY,
			int maxBuildY,
			int gameDuration,
			ServerWorld world,
			File file,
			float minBlockSize
	) {
		super(structure, spawnPositions, centrePos, absoluteCentrePos, blockProtectionOverlay, minBuildY, maxBuildY, gameDuration, world, file);
		this.disallowedMorphs = Set.of();
		this.minBlockSize = minBlockSize;
	}

	/// Constructor used only for saving the map to file
	public PropHuntGameMap(
			StructureTemplate structure,
			Set<SpawnPosition> spawnPositions,
			BlockPos centrePos,
			@Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
			Map<String, Integer> fields
	) {
		super(structure, spawnPositions, centrePos, blockProtectionOverlay, fields);
		this.disallowedMorphs = Set.of();
	}

	public boolean allowsMorph(BlockState morph, World world) {
		return !this.disallowedMorphs.contains(morph.getBlock()) && calculateBlockVolume(morph, world) >= this.minBlockSize;
	}

	public int getMinimumMorphPercentage() {
		return (int)(this.minBlockSize * 100f);
	}

	public static Optional<PropHuntGameMap> fromNbt(NbtCompound compound, ServerWorld world, BlockPos centrePos, File file) {
		final StructureTemplateManager templateManager = world.getStructureTemplateManager();
		final Optional<NbtCompound> structureCompound = compound.getCompound(STRUCTURE_KEY);
		final StructureTemplate template = structureCompound.map(templateManager::createTemplate).orElse(null);

		final Optional<Long> centrePosLong = compound.getLong(CENTRE_POS_KEY);
		if (centrePosLong.isEmpty()) {
			SocWars.LOGGER.error("Failed to load centre position for map; aborting load");
			return Optional.empty();
		}

		final Set<SpawnPosition> spawns = compound.getListOrEmpty(SpawnPosition.LIST_KEY).stream().map(element -> new SpawnPosition(element.asCompound().orElseThrow())).collect(Collectors.toSet());

		return Optional.of(new PropHuntGameMap(
				template,
				spawns,
				BlockPos.fromLong(centrePosLong.get()),
				centrePos,
				SparseVoxelOctree.fromNbtBooleanOnly(BLOCK_PROTECTION_OVERLAY_KEY, compound),
				compound.getInt(MIN_BUILD_Y_KEY, 0) + centrePos.getY(),
				compound.getInt(MAX_BUILD_Y_KEY, 60) + centrePos.getY(),
				compound.getInt(GAME_DURATION_KEY, DEFAULT_GAME_DURATION) + centrePos.getY(),
				world,
				file,
				compound.getFloat(MIN_BLOCK_SIZE_KEY, 0)
		));
	}

	@Override
	public NbtCompound toNbt(NbtCompound compound) {
		super.toNbt(compound);

		compound.putFloat(MIN_BLOCK_SIZE_KEY, this.minBlockSize);

		return compound;
	}

	@Override
	protected Map<String, RangedIntField> getMapFields() {
		return MAP_FIELDS;
	}

	private static void setMinBlockSize(AbstractGameMap abstractGameMap, int minBlockSize) {
		((PropHuntGameMap)abstractGameMap).minBlockSize = (float)minBlockSize * 0.01f;
	}
}
