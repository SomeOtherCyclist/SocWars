package com.soc.game.map;

import com.soc.game.manager.GameType;
import com.soc.lib.InfoList;
import com.soc.nbt.SkywarsChest;
import com.soc.nbt.SpawnPosition;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public record MapCheckResults(Set<SpawnPosition> spawnPositions, Set<BlockPos> centrePositions, Set<Direction> flaggedFaces, Set<BlockPos> diamondGens, Set<BlockPos> emeraldGens, Set<BlockPos> islandGens, Set<BlockPos> bedPositions, Set<BlockPos> individualShops, Set<BlockPos> teamShops, Set<SkywarsChest> lootChests, Set<BlockPos> powerups) {
    public InfoList generateWarnings(GameType mapType) {
        final InfoList warnings = new InfoList();

        warnings.add(
                () -> this.centrePositions.size() != 1,
                InfoList.InfoType.ERROR,
                Text.translatable(centrePositions.isEmpty() ? "map_block.results.no_centre" : "map_block.results.multiple_centres").formatted(Formatting.DARK_RED),
                Text.translatable(this.centrePositions.isEmpty() ? "map_block.info.no_centre" : "map_block.info.multiple_centres"));
        warnings.add(
                () -> {
                    List<SpawnPosition> filteredSpawns = spawnPositions.stream().filter(spawn -> spawn.colour() != 16).toList(); //Filter to ignore duplicates of unassigned spawns
                    return filteredSpawns.stream().distinct().count() != filteredSpawns.size();
                },
                InfoList.InfoType.ERROR,
                Text.translatable("map_block.results.duplicate_spawn_teams").formatted(Formatting.RED)
        );
        warnings.add(
                () -> this.spawnPositions.isEmpty() && mapType != GameType.HIDE_AND_SEEK,
                InfoList.InfoType.ERROR,
                Text.translatable("map_block.results.no_spawns").formatted(Formatting.RED)
        );
        warnings.add(
                () -> this.spawnPositions.stream().anyMatch(spawn -> spawn.colour() == 16),
                InfoList.InfoType.WARNING,
                () -> Text.translatable("map_block.results.spawn_missing_teams").formatted(Formatting.YELLOW),
                () -> this.spawnPositions.stream().filter(spawn -> spawn.colour() == 16).map(spawn -> getBlockPosText(spawn.pos())).toArray(Text[]::new)
        );
        warnings.add(
                () -> !this.flaggedFaces.isEmpty(),
                InfoList.InfoType.WARNING,
                () -> Text.translatable("map_block.results.blocks_bordering_area").formatted(Formatting.YELLOW),
                () -> this.flaggedFaces.stream().map(direction -> Text.translatable("direction." + direction.asString())).toArray(Text[]::new)
        );

        switch (mapType) {
            case BEDWARS -> {
                warnings.add(
                        this.bedPositions::isEmpty,
                        InfoList.InfoType.ERROR,
                        Text.translatable("map_block.results.no_beds").formatted(Formatting.DARK_RED),
                        Text.translatable("map_block.results.no_beds.hover")
                );
                warnings.add(
                        () -> this.spawnPositions.size() != this.bedPositions.size(),
                        InfoList.InfoType.WARNING,
                        Text.translatable("map_block.results.mismatched_beds").formatted(Formatting.YELLOW)
                );
                warnings.add(
                        () -> this.spawnPositions.size() != this.islandGens.size(),
                        InfoList.InfoType.WARNING,
                        Text.translatable("map_block.results.mismatched_generators").formatted(Formatting.YELLOW)
                );
                warnings.add(
                        this.diamondGens::isEmpty,
                        InfoList.InfoType.WARNING,
                        Text.translatable("map_block.results.no_diamond_gens").formatted(Formatting.YELLOW)
                );
                warnings.add(
                        this.emeraldGens::isEmpty,
                        InfoList.InfoType.WARNING,
                        Text.translatable("map_block.results.no_emerald_gens").formatted(Formatting.YELLOW)
                );
            }
            case SKYWARS -> {
                warnings.add(
                        this.lootChests::isEmpty,
                        InfoList.InfoType.WARNING,
                        Text.translatable("map_block.results.no_loot_chests").formatted(Formatting.YELLOW)
                );
            }
            case PROP_HUNT -> {
                this.addGeneralHidingWarnings(warnings);
            }
            case HIDE_AND_SEEK -> {
                this.addGeneralHidingWarnings(warnings);
            }
        }

        warnings.add(
                warnings::isEmpty,
                null, Text.translatable("map_block.results.no_issues").formatted(Formatting.DARK_GREEN)
        );

        return warnings;
    }

    private void addGeneralHidingWarnings(InfoList warnings) {
        warnings.add(
                () -> this.spawnPositions.stream().noneMatch(spawn -> spawn.dyeColour() == HideAndSeekGameMap.SEEKER_COLOUR),
                InfoList.InfoType.ERROR,
                Text.translatable("map_block.results.no_seeker_spawns").formatted(Formatting.DARK_RED),
                Text.translatable("map_block.results.no_seeker_spawns.hover")
        );
        warnings.add(
                () -> this.spawnPositions.stream().noneMatch(spawn -> spawn.dyeColour() == HideAndSeekGameMap.HIDER_COLOUR),
                InfoList.InfoType.ERROR,
                Text.translatable("map_block.results.no_hider_spawns").formatted(Formatting.DARK_RED),
                Text.translatable("map_block.results.no_hider_spawns.hover")
        );
        warnings.add(
                () -> this.spawnPositions.stream().anyMatch(spawn -> spawn.dyeColour() != HideAndSeekGameMap.SEEKER_COLOUR && spawn.dyeColour() != HideAndSeekGameMap.HIDER_COLOUR),
                InfoList.InfoType.WARNING,
                Text.translatable("map_block.results.ignored_spawns").formatted(Formatting.YELLOW),
                Text.translatable("map_block.results.ignored_spawns.hover", Text.translatable("game.seeker").formatted(formattingColourFromDye(HideAndSeekGameMap.SEEKER_COLOUR)), Text.translatable("game.hider").formatted(formattingColourFromDye(HideAndSeekGameMap.HIDER_COLOUR)))
        );
    }

    public InfoList generateResults(GameType mapType) {
        final InfoList results = new InfoList();

        results.add(
                () -> this.centrePositions.size() == 1,
                InfoList.InfoType.INFO,
                () -> {
                    final BlockPos centre = this.centrePositions.stream().findAny().get(); //This should never have issues
                    return Text.translatable("map_block.results.centre", centre.getX(), centre.getY(), centre.getZ()).formatted(Formatting.GREEN);
                },
                () -> new Text[0]
        );
        results.add(
                () -> mapType != GameType.BEDWARS && mapType != GameType.HIDE_AND_SEEK,
                InfoList.InfoType.INFO, Text.translatable("map_block.results.spawn_positions", this.spawnPositions.size()).formatted(Formatting.GREEN),
                this.getSpawnPositionsHoverText()
        );

        switch (mapType) {
            case BEDWARS -> {
                results.add(
                        () -> this.spawnPositions.stream().filter(spawn -> spawn.colour() != 16).count() == this.islandGens.size(),
                        InfoList.InfoType.INFO,
                        () -> {
                            int islands = this.spawnPositions.size();
                            return Text.translatable("map_block.results.islands", islands).formatted(Arrays.stream(new int[]{2, 4, 8}).anyMatch(count -> count == islands) ? Formatting.DARK_GREEN : Formatting.GREEN);
                        },
                        this::getSpawnPositionsHoverText
                );
                results.add(
                        () -> !this.diamondGens.isEmpty(),
                        InfoList.InfoType.INFO,
                        Text.translatable("map_block.results.diamond_gens", this.diamondGens.size()).formatted(Formatting.GREEN)
                );
                results.add(
                        () -> !this.emeraldGens.isEmpty(),
                        InfoList.InfoType.INFO,
                        Text.translatable("map_block.results.emerald_gens", this.emeraldGens.size()).formatted(Formatting.GREEN)
                );
            }
            case SKYWARS -> {
                for (int i = 0; i < 3; i++) {
                    int finalI = i;
                    results.add(
                            () -> this.lootChests.stream().anyMatch(chest -> chest.tier() == finalI),
                            InfoList.InfoType.INFO,
                            Text.translatable("map_block.results.tier_" + (finalI + 1) + "_chests", this.lootChests.stream().filter(chest -> chest.tier() == finalI).count()).formatted(Formatting.GREEN)
                    );
                }
            }
            case PROP_HUNT -> {
                this.addGeneralHidingInfo(results);
            }
            case HIDE_AND_SEEK -> {
                this.addGeneralHidingInfo(results);
                results.add(
                        () -> !this.powerups.isEmpty(),
                        InfoList.InfoType.INFO,
                        Text.translatable("map_block.results.powerups"),
                        this.powerups.stream().map(MapCheckResults::getBlockPosText).toArray(Text[]::new)
                );
            }
        }

        return results;
    }

    private void addGeneralHidingInfo(InfoList results) {
        final List<SpawnPosition> seekerSpawns = this.spawnPositions.stream().filter(spawn -> spawn.dyeColour() == HideAndSeekGameMap.SEEKER_COLOUR).toList();
        final List<SpawnPosition> hiderSpawns = this.spawnPositions.stream().filter(spawn -> spawn.dyeColour() == HideAndSeekGameMap.HIDER_COLOUR).toList();

        results.add(
                () -> !seekerSpawns.isEmpty(),
                InfoList.InfoType.INFO,
                Text.translatable("map_block.results.seeker_spawns", seekerSpawns.size()).formatted(formattingColourFromDye(HideAndSeekGameMap.SEEKER_COLOUR)),
                seekerSpawns.stream().map(spawnPosition -> getBlockPosText(spawnPosition.pos())).toArray(Text[]::new)
        );
        results.add(
                () -> !hiderSpawns.isEmpty(),
                InfoList.InfoType.INFO,
                Text.translatable("map_block.results.hider_spawns", hiderSpawns.size()).formatted(formattingColourFromDye(HideAndSeekGameMap.HIDER_COLOUR)),
                hiderSpawns.stream().map(spawnPosition -> getBlockPosText(spawnPosition.pos())).toArray(Text[]::new)
        );
    }

    public InfoList generateInfo(GameType mapType) {
        final InfoList info = this.generateResults(mapType);
        info.addEmpty(!info.isEmpty());

        return info.concat(this.generateWarnings(mapType));
    }

    private BlockPos getSingleCentre() {
        if (this.centrePositions.size() != 1) throw new IllegalStateException("Tried to access options relative position function while there are multiple centres"); //Should be unreachable
        return this.centrePositions.stream().findFirst().get();
    }

    public Set<BlockPos> getRelative(Set<BlockPos> positions) {
        return positions.stream().map(pos -> pos.subtract(this.getSingleCentre())).collect(Collectors.toSet());
    }

    public <T extends SubtractPos<T>> Set<T> getRelativeGeneric(Set<T> positions) {
        return positions.stream().map(gen -> gen.subtractPos(this.getSingleCentre())).collect(Collectors.toSet());
    }

    private Text[] getSpawnPositionsHoverText() {
        return this.spawnPositions.stream().filter(spawn -> spawn.colour() < 16).map(spawn -> Text.translatable("color.minecraft." + spawn.dyeColour().toString()).formatted(formattingColourFromDye(spawn.dyeColour()))).toArray(Text[]::new); //Sort radially?
    }

    private static Text getBlockPosText(BlockPos pos) {
        return Text.translatable("block_pos", pos.getX(), pos.getY(), pos.getZ());
    }
}
