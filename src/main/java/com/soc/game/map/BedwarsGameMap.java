package com.soc.game.map;

import com.google.common.collect.*;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class BedwarsGameMap extends AbstractGameMap {
    private final ImmutableSet<ResourceGenerator> diamondGens;
    private final ImmutableSet<ResourceGenerator> emeraldGens;
    private final ImmutableMap<Team, ResourceGenerator[]> islandGens;
    private final ImmutableMap<Team, BedPos> bedPositions;

    public BedwarsGameMap(
            StructureTemplate structure,
            Set<BlockPos> spawnLocations,
            BlockPos centrePos,
            Multimap<Team, ServerPlayerEntity> teams,
            World world,
            Set<BlockPos> diamondGens,
            Set<BlockPos> emeraldGens,
            Set<BlockPos> islandGens,
            Set<BedPos> bedPositions
    ) {
        super(structure, spawnLocations, centrePos, teams, world);
        this.diamondGens = ResourceGenerator.resourceGenerators(Items.DIAMOND.getDefaultStack(), world, diamondGens);
        this.emeraldGens = ResourceGenerator.resourceGenerators(Items.EMERALD.getDefaultStack(), world, emeraldGens);
        this.islandGens = this.makeIslandGenerators(world, islandGens, teams);
        this.bedPositions = AbstractGameMap.mapFromCollections(teams.keySet(), bedPositions);
    }

    // = NbtIo.read(path);

    /*
    public BedwarsGameMap(NbtCompound nbt, Multimap<Team, ServerPlayerEntity> teams, World world) throws IOException {
        this(
                (StructureTemplate) nbt.get("structure"),
                (Set) nbt.get("spawn_locations"),
                (BlockPos) nbt.get("centre_pos"),
                teams,
                world,
                nbt.get("diamond_gens"),
                nbt.get("emerald_gens"),
                nbt.get("island_gens"),
                nbt.get("bed_positions")
        );
    }
    */

    private ImmutableMap<Team, ResourceGenerator[]> makeIslandGenerators(World world, Set<BlockPos> islandGens, Multimap<Team, ServerPlayerEntity> teams) {
        final List<BlockPos> islandGenList = islandGens.stream().toList();

        final Set<Team> allTeams = teams.keySet();
        allTeams.addAll(Arrays.stream(new Team[islandGens.size() - teams.keySet().size()]).toList());
        final List<Team> allTeamList = allTeams.stream().toList();

        final ImmutableMap.Builder<Team, ResourceGenerator[]> builder = ImmutableMap.builder();

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
