package com.soc.game.map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.TeamCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BedwarsGameMap extends AbstractGameMap {
    private final SpreadRules spreadRules;
    private final ImmutableSet<ResourceGenerator> diamondGens;
    private final ImmutableSet<ResourceGenerator> emeraldGens;
    private final ImmutableMap<Team, ResourceGenerator[]> islandGens;
    private final ImmutableMap<Team, BedPos> bedPositions;

    public BedwarsGameMap(
            StructureTemplate structure,
            ImmutableSet<BlockPos> spawnLocations,
            World world,
            SpreadRules spreadRules,
            ImmutableSet<BlockPos> diamondGens,
            ImmutableSet<BlockPos> emeraldGens,
            ImmutableSet<BlockPos> islandGens,
            ImmutableSet<BedPos> bedPositions
    ) {
        super(structure, spawnLocations, world);
        this.spreadRules = spreadRules;
        this.diamondGens = ResourceGenerator.resourceGenerators(Items.DIAMOND.getDefaultStack(), world, diamondGens);
        this.emeraldGens = ResourceGenerator.resourceGenerators(Items.EMERALD.getDefaultStack(), world, emeraldGens);
        this.islandGens = this.makeIslandGenerators(world, islandGens);
        this.bedPositions = AbstractGameMap.mapFromCollections(this.teams, bedPositions);
    }

    private ImmutableMap<Team, ResourceGenerator[]> makeIslandGenerators(World world, ImmutableSet<BlockPos> islandGens) {
        final ImmutableMap<Team, BlockPos> generatorPositions = AbstractGameMap.mapFromCollections(super.teams, islandGens);

        final ImmutableMap.Builder<Team, ResourceGenerator[]> builder = ImmutableMap.builder();

        super.teams.forEach(team -> {
            final BlockPos pos = generatorPositions.get(team);

            builder.put(team, new ResourceGenerator[]{
                    new ResourceGenerator(Items.IRON_INGOT.getDefaultStack(), world, pos),
                    new ResourceGenerator(Items.GOLD_INGOT.getDefaultStack(), world, pos),
                    new ResourceGenerator(Items.EMERALD.getDefaultStack(), world, pos),
            });
        });
        return builder.build();
    }

    @Override
    public void spreadPlayers(ArrayList<ServerPlayerEntity> players) {
        final int numTeams = spreadRules.numTeams();

        /*
        final Stack<ServerPlayerEntity> playerStack = getRandomPlayerStack(players);

        final int numTeams = this.spreadRules.numTeams();
        final List<BlockPos> spawnLocations = super.getRandomSpawnLocationList().subList(0, numTeams);

        for (int i = 0; i < players.size(); i++) {
            final ServerPlayerEntity player = playerStack.pop();
            player.setPosition(spawnLocations.get(i % numTeams).toBottomCenterPos());
        }
        */
    }

    @Override
    public void tick() {
        this.islandGens.forEach((team, gen) -> Arrays.stream(gen).iterator().forEachRemaining(ResourceGenerator::tick));
        this.diamondGens.forEach(ResourceGenerator::tick);
        this.emeraldGens.forEach(ResourceGenerator::tick);
    }

    @Override
    public ImmutableSet<Team> makeTeams() {
        ImmutableSet.Builder<Team> builder = ImmutableSet.builder();

        for (Formatting colour : new Formatting[]{
                Formatting.RED,
                Formatting.YELLOW,
                Formatting.GREEN,
                Formatting.BLUE
        }) builder.add(super.addTeamFromFormatting(colour));

        return builder.build();
    }
}
