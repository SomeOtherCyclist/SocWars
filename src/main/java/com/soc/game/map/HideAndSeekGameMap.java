package com.soc.game.map;

import com.soc.SocWars;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SpawnPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public class HideAndSeekGameMap extends AbstractHidingGameMap {
    public static final String FILE_EXTENSION = "hsmap";

    public static final String POWERUPS_KEY = "powerups";

    private final List<BlockPos> powerups;
    private long nextPowerupTime;

    public HideAndSeekGameMap(
            StructureTemplate structure,
            Set<SpawnPosition> spawnPositions,
            BlockPos centrePos,
            BlockPos absoluteCentrePos,
            @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
            List<BlockPos> powerups,
            int minBuildY,
            int maxBuildY,
            ServerWorld world,
            File file
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, blockProtectionOverlay, minBuildY, maxBuildY, world, file);
        this.powerups = powerups;
        this.nextPowerupTime = this.getNextPowerupTime(world);
    }

    /// Constructor used only for saving the map to file
    public HideAndSeekGameMap(
            StructureTemplate structure,
            Set<SpawnPosition> spawnPositions,
            BlockPos centrePos,
            @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
            Set<BlockPos> powerups,
            Map<String, Integer> fields
    ) {
        super(structure, spawnPositions, centrePos, blockProtectionOverlay, fields);
        this.powerups = powerups.stream().toList();
        //Not going to bother initialising nextPowerupTime
    }

    public static Optional<HideAndSeekGameMap> fromNbt(NbtCompound compound, ServerWorld world, BlockPos centrePos, File file) {
        final StructureTemplateManager templateManager = world.getStructureTemplateManager();
        final Optional<NbtCompound> structureCompound = compound.getCompound(STRUCTURE_KEY);
        final StructureTemplate template = structureCompound.map(templateManager::createTemplate).orElse(null);

        final Optional<Long> centrePosLong = compound.getLong(CENTRE_POS_KEY);
        if (centrePosLong.isEmpty()) {
            SocWars.LOGGER.error("Failed to load centre position for map; aborting load");
            return Optional.empty();
        }

        final Set<SpawnPosition> spawns = compound.getListOrEmpty(SpawnPosition.LIST_KEY).stream().map(element -> new SpawnPosition(element.asCompound().orElseThrow())).collect(Collectors.toSet());

        return Optional.of(new HideAndSeekGameMap(
                template,
                spawns,
                BlockPos.fromLong(centrePosLong.get()),
                centrePos,
                SparseVoxelOctree.fromNbtBooleanOnly(BLOCK_PROTECTION_OVERLAY_KEY, compound),
                getBlockPosCollection(compound, POWERUPS_KEY, Collectors.toList()).orElseGet(() -> { SocWars.LOGGER.error("Failed to load powerups"); return List.of(); }),
                compound.getInt(MIN_BUILD_Y_KEY, 0) + centrePos.getY(),
                compound.getInt(MAX_BUILD_Y_KEY, 60) + centrePos.getY(),
                world,
                file
        ));
    }

    @Override
    public NbtCompound toNbt(NbtCompound compound) {
        super.toNbt(compound);

        putBlockPosCollection(compound, POWERUPS_KEY, this.powerups);

        return compound;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.getTime() > this.nextPowerupTime && !this.powerups.isEmpty()) {
            this.nextPowerupTime = this.getNextPowerupTime(this.world);
            final BlockPos powerupPos = this.getRandomPowerupPos();

        }
    }

    private BlockPos getRandomPowerupPos() {
        return this.pos(this.powerups.get(this.world.random.nextBetween(0, this.powerups.size() - 1)));
    }

    private long getNextPowerupTime(World world) {
        return world.getTime() + world.random.nextBetween(25, 35);
    }
}