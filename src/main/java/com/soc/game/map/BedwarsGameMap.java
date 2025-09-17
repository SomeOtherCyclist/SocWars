package com.soc.game.map;

import com.google.common.collect.*;
import com.soc.SocWars;
import com.soc.lib.SocWarsLib;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public class BedwarsGameMap extends AbstractGameMap {
    public static final String FILE_EXTENSION = "bwmap";
    public static final String DIAMOND_GENS_KEY = "diamond_gens";
    public static final String EMERALD_GENS_KEY = "emerald_gens";
    public static final String ISLAND_GENS_KEY = "island_gens";
    public static final String BED_POSITIONS_KEY = "bed_positions";

    private final ImmutableSet<ResourceGenerator> diamondGens;
    private final ImmutableSet<ResourceGenerator> emeraldGens;
    private final ImmutableMap<DyeColor, ResourceGenerator[]> islandGens;
    private final ImmutableMap<DyeColor, BlockPos> bedPositions;

    public BedwarsGameMap(
            StructureTemplate structure,
            @NotNull ImmutableMap<DyeColor, BlockPos> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull BlockPos absoluteCentrePos,
            @NotNull ServerWorld world,
            @NotNull Set<BlockPos> diamondGens,
            @NotNull Set<BlockPos> emeraldGens,
            @NotNull Set<BlockPos> islandGens,
            @NotNull Set<BlockPos> bedPositions
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, world);
        this.diamondGens = ResourceGenerator.resourceGenerators(Items.DIAMOND.getDefaultStack(), world, Set.copyOf(diamondGens.stream().map(super::pos).toList()));
        this.emeraldGens = ResourceGenerator.resourceGenerators(Items.EMERALD.getDefaultStack(), world, Set.copyOf(emeraldGens.stream().map(super::pos).toList()));
        this.islandGens = this.makeIslandGenerators(world, Set.copyOf(islandGens.stream().map(super::pos).toList()), spawnPositions.keySet()); //Double check that this works
        this.bedPositions = mapFromCollections(spawnPositions.keySet(), bedPositions); //Double check that this works
    }

    /// Constructor used only for saving the map to file
    public BedwarsGameMap(
            StructureTemplate structure,
            @NotNull ImmutableMap<DyeColor, BlockPos> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull Set<BlockPos> diamondGens,
            @NotNull Set<BlockPos> emeraldGens,
            @NotNull Set<BlockPos> islandGens,
            @NotNull Set<BlockPos> bedPositions
    ) {
        super(structure, spawnPositions, centrePos);
        this.diamondGens = ResourceGenerator.resourceGenerators(Items.DIAMOND.getDefaultStack(), world, Set.copyOf(diamondGens.stream().map(super::pos).toList()));
        this.emeraldGens = ResourceGenerator.resourceGenerators(Items.EMERALD.getDefaultStack(), world, Set.copyOf(emeraldGens.stream().map(super::pos).toList()));
        this.islandGens = this.makeIslandGenerators(world, Set.copyOf(islandGens.stream().map(super::pos).toList()), spawnPositions.keySet()); //Double check that this works
        this.bedPositions = mapFromCollections(spawnPositions.keySet(), bedPositions); //Double check that this works
    }

    public static Optional<BedwarsGameMap> loadRandomMap(@NotNull ServerWorld world, @NotNull BlockPos centrePos) {
        return loadFromFile(AbstractGameMap.getRandomMap(FILE_EXTENSION, world, null), world, centrePos);
    }

    public static Optional<BedwarsGameMap> loadFromFile(File file, @NotNull ServerWorld world, @NotNull BlockPos centrePos) {
        NbtCompound compound = null;
        try {
            compound = NbtIo.read(file.toPath());
        } catch (IOException e) {
            SocWars.LOGGER.error("Could not read compound at {}", file.getAbsolutePath());
        }

        if (compound == null) return Optional.empty();

        return fromNbt(compound, world, centrePos);
    }

    private static Optional<BedwarsGameMap> fromNbt(@NotNull NbtCompound compound, @NotNull ServerWorld world, @NotNull BlockPos centrePos) {
        final StructureTemplateManager templateManager = world.getStructureTemplateManager();
        final Optional<NbtCompound> structureCompound = compound.getCompound(STRUCTURE_KEY);
        final StructureTemplate template = structureCompound.map(templateManager::createTemplate).orElse(null);

        final Optional<Long> centrePosLong = compound.getLong(CENTRE_POS_KEY);
        if (centrePosLong.isEmpty()) {
            SocWars.LOGGER.error("Failed to load centre position for map; aborting load");
            return Optional.empty();
        }

        final Set<BlockPos> spawn_positions = getBlockPosSet(compound, SPAWN_POSITIONS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load spawn position positions"); return Set.of(); });
        final Set<DyeColor> spawn_teams = Arrays.stream(compound.getIntArray(SPAWN_TEAMS_KEY).orElse(new int[0])).mapToObj(SocWarsLib::dyeColourFromOrdinal).collect(Collectors.toSet());

        return Optional.of(new BedwarsGameMap(
                template,
                mapFromCollections(spawn_teams, spawn_positions),
                BlockPos.fromLong(centrePosLong.get()),
                centrePos,
                world,
                getBlockPosSet(compound, DIAMOND_GENS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load diamond gens"); return Set.of(); }),
                getBlockPosSet(compound, EMERALD_GENS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load emerald gens"); return Set.of(); }),
                getBlockPosSet(compound, ISLAND_GENS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load island gens"); return Set.of(); }),
                getBlockPosSet(compound, BED_POSITIONS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load bed positions"); return Set.of(); })
        ));
    }

    @Override
    public NbtCompound toNbt(NbtCompound compound) {
        super.toNbt(compound);

        putBlockPosCollection(compound, DIAMOND_GENS_KEY, this.diamondGens.stream().map(ResourceGenerator::getPos).toList());
        putBlockPosCollection(compound, EMERALD_GENS_KEY, this.emeraldGens.stream().map(ResourceGenerator::getPos).toList());
        putBlockPosCollection(compound, ISLAND_GENS_KEY, this.islandGens.values().stream().map(gens -> gens[0].getPos()).toList());
        putBlockPosCollection(compound, BED_POSITIONS_KEY, this.bedPositions.values());

        return compound;
    }

    private ImmutableMap<DyeColor, ResourceGenerator[]> makeIslandGenerators(ServerWorld world, Set<BlockPos> islandGens, Set<DyeColor> teams) {
        final List<BlockPos> islandGenList = islandGens.stream().toList(); //Should probably revisit this whole function at some point

        final Set<DyeColor> allTeams = new HashSet<>(teams);
        allTeams.addAll(Arrays.stream(new DyeColor[islandGens.size() - teams.size()]).toList());
        final List<DyeColor> allTeamList = allTeams.stream().toList();

        final ImmutableMap.Builder<DyeColor, ResourceGenerator[]> builder = ImmutableMap.builder();

        for (int i = 0; i < islandGens.size(); i++) {
            builder.put(allTeamList.get(i), new ResourceGenerator[]{
                    new ResourceGenerator(Items.IRON_INGOT.getDefaultStack(), world, islandGenList.get(i)),
                    new ResourceGenerator(Items.GOLD_INGOT.getDefaultStack(), world, islandGenList.get(i)),
                    new ResourceGenerator(Items.EMERALD.getDefaultStack(), world, islandGenList.get(i)),
            });
        }

        return builder.build();
    }

    @Override
    public void tick() {
        this.islandGens.forEach((team, gen) -> Arrays.stream(gen).iterator().forEachRemaining(ResourceGenerator::tick));
        this.diamondGens.forEach(ResourceGenerator::tick);
        this.emeraldGens.forEach(ResourceGenerator::tick);
    }
}
