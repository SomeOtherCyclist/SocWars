package com.soc.game.map;

import com.google.common.collect.ImmutableMap;
import com.soc.game.manager.GameType;
import com.soc.lib.InfoList;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.collectionPairToLeftList;
import static com.soc.lib.SocWarsLib.dyeColourFromOrdinal;

public record MapCheckResults(Set<Pair<Integer, BlockPos>> spawnPositions, Set<BlockPos> centrePositions, Set<Direction> flaggedFaces, Set<BlockPos> diamondGens, Set<BlockPos> emeraldGens, Set<BlockPos> islandGens, Set<BlockPos> bedPositions) {
    public InfoList generateWarnings(GameType mapType) {
        InfoList warnings = new InfoList();

        warnings.add(
                () -> this.centrePositions.size() != 1,
                Text.translatable(centrePositions.isEmpty() ? "map_block.results.no_centre" : "map_block.results.multiple_centres").formatted(Formatting.DARK_RED),
                new Text[] { Text.translatable(centrePositions.isEmpty() ? "map_block.info.no_centre" : "map_block.info.multiple_centres") },
                InfoList.InfoType.ERROR
        );
        warnings.add(
                () -> {
                    List<Pair<Integer, BlockPos>> filteredSpawns = spawnPositions.stream().filter(spawn -> spawn.getLeft() != 16).toList(); //Filter to ignore duplicates of unassigned spawns
                    List<Integer> teamList = collectionPairToLeftList(filteredSpawns);
                    Set<Integer> teamSet = Set.copyOf(teamList);

                    return teamList.size() != teamSet.size();
                },
                Text.translatable("map_block.results.duplicate_spawn_teams").formatted(Formatting.RED),
                new Text[0],
                InfoList.InfoType.ERROR
        );
        warnings.add(
                this.spawnPositions::isEmpty,
                Text.translatable("map_block.results.no_spawns").formatted(Formatting.RED),
                new Text[0],
                InfoList.InfoType.ERROR
        );
        warnings.add(
                () -> this.spawnPositions.stream().anyMatch(spawn -> spawn.getLeft() == 16),
                () -> Text.translatable("map_block.results.spawn_missing_teams").formatted(Formatting.YELLOW),
                () -> {
                    List<BlockPos> positions = this.spawnPositions.stream().filter(spawn -> spawn.getLeft() == 16).map(Pair::getRight).toList();

                    ArrayList<Text> text = new ArrayList<>();
                    positions.forEach(pos -> text.add(Text.translatable("block_pos", pos.getX(), pos.getY(), pos.getZ())));
                    return text.toArray(Text[]::new);
                },
                InfoList.InfoType.WARNING
        );
        warnings.add(
                () -> !this.flaggedFaces.isEmpty(),
                () -> Text.translatable("map_block.results.blocks_bordering_area").formatted(Formatting.YELLOW),
                () -> this.flaggedFaces.stream().map(direction -> Text.translatable("direction." + direction.asString())).toArray(Text[]::new),
                InfoList.InfoType.WARNING
        );

        switch (mapType) {
            case BEDWARS -> {
                warnings.add(
                        () -> this.spawnPositions.size() != this.islandGens.size(),
                        Text.translatable("map_block.results.mismatched_generators").formatted(Formatting.YELLOW),
                        new Text[0],
                        InfoList.InfoType.WARNING
                );
                warnings.add(
                        this.diamondGens::isEmpty,
                        Text.translatable("map_block.results.no_diamond_gens").formatted(Formatting.YELLOW),
                        new Text[0],
                        InfoList.InfoType.WARNING
                );
                warnings.add(
                        this.emeraldGens::isEmpty,
                        Text.translatable("map_block.results.no_emerald_gens").formatted(Formatting.YELLOW),
                        new Text[0],
                        InfoList.InfoType.WARNING
                );
            }
        }

        warnings.add(
                warnings::isEmpty,
                Text.translatable("map_block.results.no_issues").formatted(Formatting.DARK_GREEN),
                new Text[0],
                null
        );

        return warnings;
    }

    public InfoList generateResults(GameType mapType) {
        InfoList results = new InfoList();

        results.add(
                () -> centrePositions.size() == 1,
                () -> {
                    BlockPos centre = centrePositions.stream().findAny().get(); //This should never have issues
                    return Text.translatable("map_block.results.centre", centre.getX(), centre.getY(), centre.getZ()).formatted(Formatting.GREEN);
                },
                () -> new Text[0],
                InfoList.InfoType.INFO
        );
        results.add(
                () -> mapType != GameType.BEDWARS,
                Text.translatable("map_block.results.spawn_positions", spawnPositions.size()).formatted(Formatting.GREEN),
                new Text[0],
                InfoList.InfoType.INFO
        );

        switch (mapType) {
            case BEDWARS -> {
                results.add(
                        () -> {
                            List<Pair<Integer, BlockPos>> validSpawnPositions = spawnPositions.stream().filter(spawn -> spawn.getLeft() != 16).toList();
                            return validSpawnPositions.size() == this.islandGens.size();
                        },
                        () -> {
                            int islands = this.spawnPositions.size();
                            return Text.translatable("map_block.results.islands", islands).formatted(Arrays.stream(new int[]{2, 4, 8}).anyMatch(count -> count == islands) ? Formatting.DARK_GREEN : Formatting.GREEN);
                        },
                        () -> new Text[0],
                        InfoList.InfoType.INFO
                );
                results.add(
                        () -> !this.diamondGens.isEmpty(),
                        Text.translatable("map_block.results.diamond_gens", this.diamondGens.size()).formatted(Formatting.GREEN),
                        new Text[0],
                        InfoList.InfoType.INFO
                );
                results.add(
                        () -> !this.emeraldGens.isEmpty(),
                        Text.translatable("map_block.results.emerald_gens", this.emeraldGens.size()).formatted(Formatting.GREEN),
                        new Text[0],
                        InfoList.InfoType.INFO
                );
            }
        }

        return results;
    }

    public InfoList generateInfo(GameType mapType) {
        InfoList info = this.generateResults(mapType);
        info.addEmpty(() -> !info.isEmpty());

        return info.concat(this.generateWarnings(mapType));
    }

    private BlockPos getSingleCentre() {
        if (this.centrePositions.size() != 1) throw new IllegalStateException("Tried to access a relative position function while there are multiple centres");
        return this.centrePositions.stream().findFirst().get();
    }

    public Set<Pair<Integer, BlockPos>> relativeSpawnPositions() {
        return this.spawnPositions.stream().map(spawn -> Pair.of(spawn.getLeft(), spawn.getRight().subtract(this.getSingleCentre()))).collect(Collectors.toSet());
    }
    public Set<BlockPos> getRelative(Set<net.minecraft.util.math.BlockPos> positions) {
        return positions.stream().map(pos -> pos.subtract(this.getSingleCentre())).collect(Collectors.toSet());
    }

    public ImmutableMap<DyeColor, BlockPos> spawnPositionsAsMap() {
        ImmutableMap.Builder<DyeColor, BlockPos> builder = new ImmutableMap.Builder<>();
        this.spawnPositions.stream().forEach(spawn -> builder.put(dyeColourFromOrdinal(spawn.getLeft()), spawn.getRight()));
        return builder.build();
    }
    public ImmutableMap<DyeColor, BlockPos> relativeSpawnPositionsAsMap() {
        ImmutableMap.Builder<DyeColor, BlockPos> builder = new ImmutableMap.Builder<>();
        this.relativeSpawnPositions().stream().forEach(spawn -> builder.put(dyeColourFromOrdinal(spawn.getLeft()), spawn.getRight()));
        return builder.build();
    }

    /*
    BlockPos closestSpawnPos = spawnPositions.values().stream().min(Comparator.comparing(pos -> pos.getSquaredDistance(currentPos))).get(); //This is going to need work
        islandGens.put(spawnPositions.inverse().get(closestSpawnPos), currentPos);
    */
}
