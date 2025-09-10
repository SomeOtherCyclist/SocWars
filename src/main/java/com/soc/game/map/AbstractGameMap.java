package com.soc.game.map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.soc.SocWars;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public abstract class AbstractGameMap {
    public static final String MAP_FILE_EXTENSION = "socmap";

    protected final StructureTemplate structure;
    protected final BlockPos centrePos;
    protected final ImmutableMap<Team, BlockPos> spawnLocations;

    protected final World world;
    protected int tick;

    public AbstractGameMap(
            StructureTemplate structure,
            Set<BlockPos> spawnLocations,
            BlockPos centrePos,
            Multimap<Team, ServerPlayerEntity> teams,
            World world
    ) {
        this.structure = structure;
        this.spawnLocations = mapFromCollections(teams.keySet(), spawnLocations);
        this.centrePos = centrePos.toImmutable();
        this.world = world;
    }

    public abstract void spreadPlayers(Multimap<Team, ServerPlayerEntity> teams);
    public abstract void tick();

    protected static <T, U> ImmutableMap<T, U> mapFromCollections(Collection<T> t1, Collection<U> t2) {
        ImmutableMap.Builder<T, U> builder = ImmutableMap.builder();

        Iterator<T> t1Iterator = t1.iterator();
        Iterator<U> t2Iterator = t2.iterator();

        while (t1Iterator.hasNext() && t2Iterator.hasNext()) {
            builder.put(t1Iterator.next(), t2Iterator.next());
        }

        return builder.build();
    }

    public static File getMapDirectory() {
        final File file = Path.of(
                FabricLoader.getInstance().getConfigDir().toString(),
                SocWars.MOD_ID,
                "maps"
        ).toFile();

        //Ensure that the folder exists before returning
        final boolean madeFileDir = file.mkdirs();
        SocWars.LOGGER.info(madeFileDir ? "Created maps file directory" : "Failed to create maps file directory " + (file.exists() ? "as it already exists" : "because screw you I guess?"));

        return file;
    }
    public static File[] getMaps() {
        final var files = getMapDirectory().listFiles((file) -> {
            final String[] splitName = file.getName().split("\\.");
            final String extension = splitName[splitName.length - 1];

            return Objects.equals(extension, MAP_FILE_EXTENSION);
        });

        return (files != null) ? files : new File[0];
    }
    public static Stack<ServerPlayerEntity> getRandomPlayerStack(Collection<ServerPlayerEntity> players) {
        final Stack<ServerPlayerEntity> playerStack = new Stack<>();
        Collections.shuffle((ArrayList<?>) new ArrayList<>(players).clone());
        playerStack.addAll(players);

        return playerStack;
    }
    public final BlockPos pos(BlockPos pos) {
        return pos.add(centrePos);
    }
}
