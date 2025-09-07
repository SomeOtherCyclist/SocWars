package com.soc.game.map;

import com.soc.game.manager.AbstractGameManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGameMap<T extends AbstractGameManager.GameType> {
    private static String DIRECTORY;

    protected final StructureTemplate structure;
    protected final ArrayList<BlockPos> spawnLocations;

    protected AbstractGameMap(
            StructureTemplate structure,
            ArrayList<BlockPos> spawnLocations
    ) {
        this.structure = structure;
        this.spawnLocations = spawnLocations;
    }

    private List<Path> getMaps(World world) {
        FabricLoader.getInstance().getGameDir();
        return null;
    }
}
