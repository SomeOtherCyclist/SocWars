package com.soc.game.map;

import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public record MapCheckResults(Set<Pair<Integer, BlockPos>> spawnPositions, Set<BlockPos> centrePos, Set<BlockPos> diamondGens, Set<BlockPos> emeraldGens, Set<BlockPos> islandGens) {
    public List<Text> generateWarnings() {
        return List.of();
    }

    /*
    BlockPos closestSpawnPos = spawnPositions.values().stream().min(Comparator.comparing(pos -> pos.getSquaredDistance(currentPos))).get(); //This is going to need work
        islandGens.put(spawnPositions.inverse().get(closestSpawnPos), currentPos);
    */
}
