package com.soc.game.map;

import com.soc.SocWars;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SkywarsChest;
import com.soc.nbt.SpawnPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DuelsGameMap extends AbstractGameMap {
	public static final String FILE_EXTENSION = "dm";
	private final Map<BlockPos, IngameSkywarsChest> lootChests;

	public DuelsGameMap(
			StructureTemplate structure,
			@NotNull Set<SpawnPosition> spawnPositions,
			@NotNull BlockPos centrePos,
			BlockPos absoluteCentrePos,
			@Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
			int minBuildY,
			int maxBuildY,
			ServerWorld world,
			Set<SkywarsChest> lootChests,
			File file) {
		super(structure, spawnPositions, centrePos, absoluteCentrePos, blockProtectionOverlay, minBuildY, maxBuildY, world, file);
		this.lootChests = lootChests.stream().collect(Collectors.toMap(chest -> super.pos(chest.pos()), IngameSkywarsChest::new));
	}

	public DuelsGameMap(
			StructureTemplate structure,
			@NotNull Set<SpawnPosition> spawnPositions,
			@NotNull BlockPos centrePos,
			SparseVoxelOctree<Boolean> blockProtectionOverlay,
			Set<SkywarsChest> lootChests,
			Map<String, Integer> fields
	) {
		super(structure, spawnPositions, centrePos, blockProtectionOverlay);
		this.lootChests = lootChests.stream().collect(Collectors.toMap(chest -> super.pos(chest.pos()), IngameSkywarsChest::new));
	}

	@Override
	public void tick() {

	}

	public static Optional<DuelsGameMap> fromNbt(NbtCompound compound, ServerWorld world, BlockPos centrePos, File file) {
		final StructureTemplateManager templateManager = world.getStructureTemplateManager();
		final Optional<NbtCompound> structureCompound = compound.getCompound(STRUCTURE_KEY);
		final StructureTemplate template = structureCompound.map(templateManager::createTemplate).orElse(null);

		final Optional<Long> centrePosLong = compound.getLong(CENTRE_POS_KEY);
		if (centrePosLong.isEmpty()) {
			SocWars.LOGGER.error("Failed to load centre position for map; aborting load");
			return Optional.empty();
		}

		final Set<SpawnPosition> spawns = compound.getListOrEmpty(SpawnPosition.LIST_KEY).stream().map(element -> new SpawnPosition(element.asCompound().orElseThrow())).collect(Collectors.toSet());
		final Set<SkywarsChest> chests = compound.getListOrEmpty(SkywarsChest.LIST_KEY).stream().map(element -> new SkywarsChest(element.asCompound().orElseThrow())).collect(Collectors.toSet());

		return Optional.of(new DuelsGameMap(
				template,
				spawns,
				BlockPos.fromLong(centrePosLong.get()),
				centrePos,
				SparseVoxelOctree.fromNbtBooleanOnly(BLOCK_PROTECTION_OVERLAY_KEY, compound),
				compound.getInt(MIN_BUILD_Y_KEY, 0) + centrePos.getY(),
				compound.getInt(MAX_BUILD_Y_KEY, 60) + centrePos.getY(),
				world,
				chests,
				file
		));
	}

	public Optional<IngameSkywarsChest> getLootChest(BlockPos pos) {
		return Optional.ofNullable(this.lootChests.get(pos));
	}
}
