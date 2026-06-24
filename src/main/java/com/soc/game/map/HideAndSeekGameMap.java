package com.soc.game.map;

import com.soc.SocWars;
import com.soc.entities.PowerupEntity;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SpawnPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public class HideAndSeekGameMap extends AbstractHidingGameMap {
    public static final String FILE_EXTENSION = "hsmap";
    private static final int DEFAULT_GAME_DURATION = 5 * 60 * 20;
    public static final Map<String, RangedIntField> MAP_FIELDS = buildFields(
            new RangedIntField("game_duration", 0, 24 * 60 * 60, AbstractGameMap::setGameDurationSeconds)
    );

    public static final String POWERUPS_KEY = "powerups";

    private final List<BlockPos> powerups;

    public HideAndSeekGameMap(
            StructureTemplate structure,
            Set<SpawnPosition> spawnPositions,
            BlockPos centrePos,
            BlockPos absoluteCentrePos,
            @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
            List<BlockPos> powerups,
            int minBuildY,
            int maxBuildY,
            int gameDuration,
            ServerWorld world,
            File file
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, blockProtectionOverlay, minBuildY, maxBuildY, gameDuration, world, file);
        this.powerups = powerups;
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
                compound.getInt(GAME_DURATION_KEY, DEFAULT_GAME_DURATION) + centrePos.getY(),
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
    protected Map<String, RangedIntField> getMapFields() {
        return MAP_FIELDS;
    }

    public void spawnPowerup() {
        this.world.spawnEntity(new PowerupEntity(this.world, this.pos(getRandomElement(this.powerups, this.world.random))));
    }

    public boolean hasPowerups() {
        return !this.powerups.isEmpty();
    }
}