package com.soc.game.map;

import com.google.common.collect.*;
import com.soc.SocWars;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

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
            @NotNull Set<BlockPos> spawnPositions,
            @NotNull BlockPos centrePos,
            Set<DyeColor> teams,
            ServerWorld world,
            @NotNull Set<BlockPos> diamondGens,
            @NotNull Set<BlockPos> emeraldGens,
            @NotNull Set<BlockPos> islandGens,
            @NotNull Set<BlockPos> bedPositions
    ) {
        super(structure, spawnPositions, centrePos, teams, world);
        this.diamondGens = ResourceGenerator.resourceGenerators(Items.DIAMOND.getDefaultStack(), world, Set.copyOf(diamondGens.stream().map(super::pos).toList()));
        this.emeraldGens = ResourceGenerator.resourceGenerators(Items.EMERALD.getDefaultStack(), world, Set.copyOf(emeraldGens.stream().map(super::pos).toList()));
        this.islandGens = this.makeIslandGenerators(world, Set.copyOf(islandGens.stream().map(super::pos).toList()), teams);
        this.bedPositions = mapFromCollections(teams, bedPositions);
    }

    public static Optional<BedwarsGameMap> loadRandomMap(Set<DyeColor> teams, ServerWorld world) {
        return loadFromFile(AbstractGameMap.getRandomMap(FILE_EXTENSION, world, null), teams, world);
    }

    public static Optional<BedwarsGameMap> loadFromFile(File file, Set<DyeColor> teams, ServerWorld world) {
        NbtCompound compound = null;
        try {
            compound = NbtIo.read(file.toPath());
        } catch (IOException e) {
            SocWars.LOGGER.error("Could not read compound at {}", file.getAbsolutePath());
        }

        if (compound == null) return Optional.empty();

        return fromNbt(compound, teams, world);
    }

    private static Optional<BedwarsGameMap> fromNbt(NbtCompound compound, Set<DyeColor> teams, ServerWorld world) {
        StructureTemplateManager templateManager = world.getStructureTemplateManager();
        Optional<NbtCompound> structureCompound = compound.getCompound(STRUCTURE_KEY);
        StructureTemplate template = structureCompound.map(templateManager::createTemplate).orElse(null);

        Optional<Long> centrePosLong = compound.getLong(CENTRE_POS_KEY);
        if (centrePosLong.isEmpty()) {
            SocWars.LOGGER.error("Failed to load centre position for map; aborting load");
            return Optional.empty();
        }

        return Optional.of(new BedwarsGameMap(
                template,
                getBlockPosSet(compound, SPAWN_POSITIONS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load spawn positions"); return Set.of(); }),
                BlockPos.fromLong(centrePosLong.get()),
                teams,
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

        putBlockPosSet(compound, DIAMOND_GENS_KEY, super.spawnPositions.values());
        putBlockPosSet(compound, EMERALD_GENS_KEY, super.spawnPositions.values());
        putBlockPosSet(compound, ISLAND_GENS_KEY, super.spawnPositions.values());
        putBlockPosSet(compound, BED_POSITIONS_KEY, super.spawnPositions.values());

        return compound;
    }

    public boolean saveToFile(Path path) {
        if (super.structure == null) {
            SocWars.LOGGER.info("Failed to save map as the structure is null");
            return false;
        }

        try {
            NbtIo.write(this.toNbt(new NbtCompound()), path);
        } catch (IOException e) {
            SocWars.LOGGER.info("Failed to save map due to an IOException: {}", e.getMessage());
            return false;
        }

        return true;
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
