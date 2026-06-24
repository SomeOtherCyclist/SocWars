package com.soc.game.map;

import com.soc.SocWars;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SkywarsChest;
import com.soc.nbt.SpawnPosition;
import com.soc.resourcedata.listeners.SkywarsLootData;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class SkywarsGameMap extends AbstractGameMap {
    public static final String FILE_EXTENSION = "swmap";
    private static final int DEFAULT_GAME_DURATION = 15 * 60 * 20;
    public static final Map<String, RangedIntField> MAP_FIELDS = buildFields(
            new RangedIntField("game_duration", 0, 24 * 60 * 60, AbstractGameMap::setGameDurationSeconds)
    );

    private final Map<BlockPos, IngameSkywarsChest> lootChests;

    public SkywarsGameMap(
            StructureTemplate structure,
            Set<SpawnPosition> spawnPositions,
            BlockPos centrePos,
            BlockPos absoluteCentrePos,
            SparseVoxelOctree<Boolean> blockProtectionOverlay,
            int minBuildY,
            int maxBuildY,
            int gameDuration,
            ServerWorld world,
            Set<SkywarsChest> lootChests,
            File file
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, blockProtectionOverlay, minBuildY, maxBuildY, gameDuration, world, file);
        this.lootChests = lootChests.stream().collect(Collectors.toMap(chest -> super.pos(chest.pos()), IngameSkywarsChest::new));
    }

    /// Constructor used only for saving the map to file
    public SkywarsGameMap(
            StructureTemplate structure,
            Set<SpawnPosition> spawnPositions,
            BlockPos centrePos,
            @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
            Set<SkywarsChest> lootChests,
            Map<String, Integer> fields) {
        super(structure, spawnPositions, centrePos, blockProtectionOverlay, fields);
        this.lootChests = lootChests.stream().collect(Collectors.toMap(chest -> super.pos(chest.pos()), IngameSkywarsChest::new));
    }

    public void placeLootChests() {
        this.lootChests.forEach((pos, chest) -> {
            this.world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(HorizontalFacingBlock.FACING, chest.getFacing()));
        });
    }

    public void populateInventory(int tier, BlockPos pos, int fillOrdinal) {
        SkywarsLootData.INSTANCE.getSkywarsItemData().populateInventory(tier, this.world, pos, fillOrdinal, this.starterWoolColour(tier, pos, fillOrdinal));
    }

    @Override
    protected Map<String, RangedIntField> getMapFields() {
        return MAP_FIELDS;
    }

    private Optional<DyeColor> starterWoolColour(int tier, BlockPos pos, int loadOrdinal) {
        return tier == 0 && loadOrdinal == 0 ? this.spawnPositions.entries().stream().min(Map.Entry.comparingByValue((a, b) -> {
            final double distA = super.pos(a).getSquaredDistance(pos);
            final double distB = super.pos(b).getSquaredDistance(pos);
            if (distA == distB) return 0;
            return distA < distB ? -1 : 1;
        })).map(Map.Entry::getKey) : Optional.empty();
    }

    public Optional<IngameSkywarsChest> getLootChest(BlockPos pos) {
        return Optional.ofNullable(this.lootChests.get(pos));
    }

    public static Optional<SkywarsGameMap> fromNbt(@NotNull NbtCompound compound, @NotNull ServerWorld world, @NotNull BlockPos centrePos, File file) {
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

        return Optional.of(new SkywarsGameMap(
                template,
                spawns,
                BlockPos.fromLong(centrePosLong.get()),
                centrePos,
                null,
                compound.getInt(MIN_BUILD_Y_KEY, 0) + centrePos.getY(),
                compound.getInt(MAX_BUILD_Y_KEY, 60) + centrePos.getY(),
                compound.getInt(GAME_DURATION_KEY, DEFAULT_GAME_DURATION) + centrePos.getY(),
                world,
                chests,
                file
        ));
    }

    @Override
    public NbtCompound toNbt(NbtCompound compound) {
        super.toNbt(compound);

        compound.put(SkywarsChest.LIST_KEY, this.getChestsAsNbt());

        return compound;
    }

    private NbtList getChestsAsNbt() {
        final NbtList chests = new NbtList();
        this.lootChests.forEach((pos, chest) -> chests.add(new SkywarsChest(pos, chest).toNbt()));
        return chests;
    }

    @Override
    public void tick() {}
}