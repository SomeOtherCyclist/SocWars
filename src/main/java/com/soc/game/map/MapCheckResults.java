package com.soc.game.map;

import com.soc.game.manager.GameType;
import com.soc.lib.InfoList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.soc.lib.SocWarsLib.collectionPairToLeftList;

public record MapCheckResults(Set<Pair<Integer, BlockPos>> spawnPositions, Set<BlockPos> centrePositions, Set<BlockPos> diamondGens, Set<BlockPos> emeraldGens, Set<BlockPos> islandGens) {
    public List<Pair<Text, Text>> generateWarnings(GameType mapType) {
        InfoList warnings = new InfoList();

        warnings.add(
                () -> this.centrePositions.size() != 1,
                Text.translatable(centrePositions.isEmpty() ? "map_block.results.no_centre" : "map_block.results.multiple_centres").formatted(Formatting.DARK_RED),
                Text.empty()
        );
        warnings.add(
                () -> {
                    List<Pair<Integer, BlockPos>> filteredSpawns = spawnPositions.stream().filter(spawn -> spawn.getLeft() != 16).toList(); //Filter to ignore duplicates of unassigned spawns
                    List<Integer> teamList = collectionPairToLeftList(filteredSpawns);
                    Set<Integer> teamSet = Set.copyOf(teamList);

                    return teamList.size() != teamSet.size();
                },
                Text.translatable("map_block.results.duplicate_spawn_teams").formatted(Formatting.RED),
                Text.empty()
        );
        warnings.add(
                () -> this.spawnPositions.stream().anyMatch(spawn -> spawn.getLeft() == 16),
                Text.translatable("map_block.results.spawn_missing_teams").formatted(Formatting.RED),
                Text.empty()
        );

        switch (mapType) {
            case BEDWARS -> {
                warnings.add(
                        () -> this.spawnPositions.size() != this.islandGens.size(),
                        Text.translatable("Mismatched generators").formatted(Formatting.YELLOW),
                        Text.empty()
                );
            }
        }

        warnings.add(
                warnings::isEmpty,
                Text.translatable("No issues found!").formatted(Formatting.DARK_GREEN),
                Text.empty()
        );

        return warnings.getInfo();
    }

    public List<Pair<Text, Text>> generateResults(GameType mapType) {
        InfoList results = new InfoList();

        results.add(
                () -> centrePositions.size() == 1,
                () -> {
                    BlockPos centre = centrePositions.stream().findAny().get(); //This should never have issues
                    return Text.translatable("map_block.results.centre", centre.getX(), centre.getY(), centre.getZ()).formatted(Formatting.GREEN);
                },
                () -> Text.empty()
        );

        switch (mapType) {
            case BEDWARS -> {
                results.add(
                        () -> this.spawnPositions.size() == this.islandGens.size(),
                        () -> {
                            int islands = this.spawnPositions.size();
                            return Text.translatable("map_block.results.islands", islands).formatted(Arrays.stream(new int[]{2, 4, 8}).anyMatch(count -> count == islands) ? Formatting.DARK_GREEN : Formatting.GREEN);
                        },
                        () -> Text.empty()
                );
                results.add(
                        Text.translatable("map_block.results.diamond_gens", this.diamondGens.size()).formatted(Formatting.GREEN),
                        Text.empty()
                );
                results.add(
                        Text.translatable("map_block.results.emerald_gens", this.emeraldGens.size()).formatted(Formatting.GREEN),
                        Text.empty()
                );
            }
        }

        return results.getInfo();
    }

    public List<Pair<Text, Text>> generateInfo(GameType mapType) {
        List<Pair<Text, Text>> info = this.generateResults(mapType);
        if (!info.isEmpty()) info.add(Pair.of(Text.empty(), Text.empty()));
        info.addAll(this.generateWarnings(mapType));

        return info;
    }

    /*
    BlockPos closestSpawnPos = spawnPositions.values().stream().min(Comparator.comparing(pos -> pos.getSquaredDistance(currentPos))).get(); //This is going to need work
        islandGens.put(spawnPositions.inverse().get(closestSpawnPos), currentPos);
    */
}
