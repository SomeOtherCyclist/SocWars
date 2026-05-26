package com.soc.game.map;

import com.soc.SocWars;
import com.soc.entities.BedwarsShopEntity;
import com.soc.game.manager.bedwars.ShopType;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SpawnPosition;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.soc.lib.SocWarsLib.*;

public class BedwarsGameMap extends AbstractGameMap {
    public static final String FILE_EXTENSION = "bwmap";
    public static final Map<String, RangedIntField> MAP_FIELDS = buildFields(
            new RangedIntField("min_build_height", -384, 384, AbstractGameMap::setMinBuildY),
            new RangedIntField("max_build_height", -384, 384, AbstractGameMap::setMaxBuildY)
    );

    public static final String DIAMOND_GENS_KEY = "diamond_gens";
    public static final String EMERALD_GENS_KEY = "emerald_gens";
    public static final String ISLAND_GENS_KEY = "island_gens";
    public static final String BED_POSITIONS_KEY = "bed_positions";
    public static final String INDIVIDUAL_SHOPS_KEY = "individual_shops";
    public static final String TEAM_SHOPS_KEY = "team_shops";

    public static final float SPLIT_RANGE = 4f;

    private final Set<ResourceGenerator> diamondGens;
    private final Set<ResourceGenerator> emeraldGens;
    private final Map<DyeColor, IslandGenerator> islandGens;
    private final Map<Integer, ResourceGenerator> resourceGeneratorIdMap;
    private final Map<DyeColor, BlockPos> bedPositions;
    private final Set<BlockPos> individualShops;
    private final Set<BlockPos> teamShops;

    public BedwarsGameMap(
            StructureTemplate structure,
            Set<SpawnPosition> spawnPositions,
            BlockPos centrePos,
            BlockPos absoluteCentrePos,
            @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
            int minBuildY,
            int maxBuildY,
            ServerWorld world,
            Set<BlockPos> diamondGens,
            Set<BlockPos> emeraldGens,
            Set<BlockPos> islandGens,
            Set<BlockPos> bedPositions,
            Set<BlockPos> individualShops,
            Set<BlockPos> teamShops,
            File file
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, blockProtectionOverlay, minBuildY, maxBuildY, world, file);
        this.diamondGens = diamondGens.stream().map(pos -> new ResourceGenerator(Items.DIAMOND, 1, world, this.pos(pos), false, 30 * 20)).collect(Collectors.toSet());
        this.emeraldGens = emeraldGens.stream().map(pos -> new ResourceGenerator(Items.EMERALD, 1, world, this.pos(pos), false, 40 * 20)).collect(Collectors.toSet());
        this.islandGens = this.makeIslandGenerators(world, islandGens.stream().map(this::pos).collect(Collectors.toSet()), spawnPositions.stream().map(spawnPosition -> spawnPosition.withPos(this.pos(spawnPosition.pos()))).collect(Collectors.toSet()));
        this.resourceGeneratorIdMap = this.assignGeneratorIds();
        this.bedPositions = this.makeBedPositions(spawnPositions, bedPositions);
        this.individualShops = individualShops;
        this.teamShops = teamShops;

        this.islandGens.values().forEach(gen -> gen.upgrade(0));
    }

    /// Constructor used only for saving the map to file
    public BedwarsGameMap(
            StructureTemplate structure,
            Set<SpawnPosition> spawnPositions,
            BlockPos centrePos,
            @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
            Set<BlockPos> diamondGens,
            Set<BlockPos> emeraldGens,
            Set<BlockPos> islandGens,
            Set<BlockPos> bedPositions,
            Set<BlockPos> individualShops,
            Set<BlockPos> teamShops,
            Map<String, Integer> fields
    ) {
        super(structure, spawnPositions, centrePos, blockProtectionOverlay);
        this.diamondGens = diamondGens.stream().map(pos -> new ResourceGenerator(Items.DIAMOND, 1, world, pos, false, 30 * 20)).collect(Collectors.toSet());
        this.emeraldGens = emeraldGens.stream().map(pos -> new ResourceGenerator(Items.EMERALD, 1, world, pos, false, 40 * 20)).collect(Collectors.toSet());
        this.islandGens = this.makeIslandGenerators(this.world, islandGens, spawnPositions);
        this.resourceGeneratorIdMap = this.assignGeneratorIds();
        this.bedPositions = this.makeBedPositions(spawnPositions, bedPositions);
        this.individualShops = individualShops;
        this.teamShops = teamShops;

        this.applyFields(fields, MAP_FIELDS);
    }

    private Map<DyeColor, BlockPos> makeBedPositions(Set<SpawnPosition> spawnPositions, Set<BlockPos> bedPositions) {
        return spawnPositions.stream().collect(Collectors.toMap(SpawnPosition::dyeColour, spawn -> findNearestBlockPos(bedPositions, spawn.pos()).get()));
    }

    private static Optional<BlockPos> findNearestBlockPos(Collection<BlockPos> positions, BlockPos origin) {
        return positions.stream().min(Comparator.comparing(pos -> pos.getSquaredDistance(origin)));
    }

    public boolean isWithinSplitRange(ServerPlayerEntity player) {
        return this.islandGens.values().stream().map(IslandGenerator::getPos).anyMatch(pos -> pos.isWithinDistance(player.getPos(), SPLIT_RANGE));
    }

    public static Optional<BedwarsGameMap> fromNbt(NbtCompound compound, ServerWorld world, BlockPos centrePos, File file) {
        final StructureTemplateManager templateManager = world.getStructureTemplateManager();
        final Optional<NbtCompound> structureCompound = compound.getCompound(STRUCTURE_KEY);
        final StructureTemplate template = structureCompound.map(templateManager::createTemplate).orElse(null);

        final Optional<Long> centrePosLong = compound.getLong(CENTRE_POS_KEY);
        if (centrePosLong.isEmpty()) {
            SocWars.LOGGER.error("Failed to load centre position for map; aborting load");
            return Optional.empty();
        }

        final Set<SpawnPosition> spawns = compound.getListOrEmpty(SpawnPosition.LIST_KEY).stream().map(element -> new SpawnPosition(element.asCompound().orElseThrow())).collect(Collectors.toSet());

        return Optional.of(new BedwarsGameMap(
                template,
                spawns,
                BlockPos.fromLong(centrePosLong.get()),
                centrePos,
                SparseVoxelOctree.fromNbtBooleanOnly(BLOCK_PROTECTION_OVERLAY_KEY, compound),
                compound.getInt(MIN_BUILD_Y_KEY, 0) + centrePos.getY(),
                compound.getInt(MAX_BUILD_Y_KEY, 60) + centrePos.getY(),
                world,
                getBlockPosSet(compound, DIAMOND_GENS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load diamond gens"); return Set.of(); }),
                getBlockPosSet(compound, EMERALD_GENS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load emerald gens"); return Set.of(); }),
                getBlockPosSet(compound, ISLAND_GENS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load island gens"); return Set.of(); }),
                getBlockPosSet(compound, BED_POSITIONS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load bed positions"); return Set.of(); }),
                getBlockPosSet(compound, INDIVIDUAL_SHOPS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load individual shops"); return Set.of(); }),
                getBlockPosSet(compound, TEAM_SHOPS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load team shops"); return Set.of(); }),
                file
        ));
    }

    @Override
    public NbtCompound toNbt(NbtCompound compound) {
        super.toNbt(compound);

        putBlockPosCollection(compound, DIAMOND_GENS_KEY, this.diamondGens.stream().map(ResourceGenerator::getPos).toList());
        putBlockPosCollection(compound, EMERALD_GENS_KEY, this.emeraldGens.stream().map(ResourceGenerator::getPos).toList());
        putBlockPosCollection(compound, ISLAND_GENS_KEY, this.islandGens.values().stream().map(IslandGenerator::getPos).toList());
        putBlockPosCollection(compound, BED_POSITIONS_KEY, this.bedPositions.values());
        putBlockPosCollection(compound, INDIVIDUAL_SHOPS_KEY, this.individualShops);
        putBlockPosCollection(compound, TEAM_SHOPS_KEY, this.teamShops);

        return compound;
    }

    private Map<DyeColor, IslandGenerator> makeIslandGenerators(ServerWorld world, Set<BlockPos> islandGens, Set<SpawnPosition> teams) {
        return islandGens.stream().collect(Collectors.toMap(
                //genPos -> teams.stream().filter(spawn -> genPos.getSquaredDistance(spawn.pos()) < 9).findAny().map(spawn -> DyeColourWithEmpty.fromDyeColour(spawn.dyeColour())).orElse(DyeColourWithEmpty.EMPTY),
                genPos -> teams.stream()
                        .filter(spawn -> spawn.pos().isWithinDistance(genPos, 10))
                        .min(Comparator.comparingDouble(spawn -> spawn.pos().getSquaredDistance(genPos)))
                        .map(SpawnPosition::dyeColour)
                        .orElse(null),
                genPos -> new IslandGenerator(world, genPos)
        ));
    }

    private Map<Integer, ResourceGenerator> assignGeneratorIds() {
        final Map<Integer, ResourceGenerator> map = this.islandGens.values().stream().flatMap(IslandGenerator::getConstituentGenerators).collect(Collectors.toMap(ResourceGenerator::getId, Function.identity()));

        this.diamondGens.forEach(gen -> map.put(gen.getId(), gen));
        this.emeraldGens.forEach(gen -> map.put(gen.getId(), gen));

        return map;
    }

    @Override
    public void placeMap() {
        super.placeMap();
        this.individualShops.forEach(pos -> BedwarsShopEntity.spawnWithPos(this.world, this.pos(pos).toBottomCenterPos(), ShopType.INDIVIDUAL));
        this.teamShops.forEach(pos -> BedwarsShopEntity.spawnWithPos(this.world, this.pos(pos).toBottomCenterPos(), ShopType.TEAM));
    }

    @Override
    public void tick() {
        this.islandGens.values().forEach(IslandGenerator::tick);
        this.diamondGens.forEach(ResourceGenerator::tick);
        this.emeraldGens.forEach(ResourceGenerator::tick);
    }

    public void upgradeDiamondGens(GeneratorStats stats) {
        this.diamondGens.forEach(gen -> gen.setStats(stats));
    }

    public void upgradeEmeraldGens(GeneratorStats stats) {
        this.emeraldGens.forEach(gen -> gen.setStats(stats));
    }

    public boolean upgradeIslandGen(DyeColor team, int tier) {
        return this.islandGens.get(team).upgrade(tier);
    }

    public Map<DyeColor, BlockPos> getBedPositions() {
        return this.bedPositions;
    }

    public BlockPos getBedPosition(DyeColor team) {
        return this.pos(this.bedPositions.get(team));
    }

    public Optional<ResourceGenerator> getResourceGenerator(int id) {
        return Optional.ofNullable(this.resourceGeneratorIdMap.get(id));
    }
}
