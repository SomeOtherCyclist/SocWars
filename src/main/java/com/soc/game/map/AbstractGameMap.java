package com.soc.game.map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.soc.SocWars;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static com.soc.SocWarsLib.mapFromCollections;
import static com.soc.SocWarsLib.putBlockPosSet;

public abstract class AbstractGameMap {
    public static final String STRUCTURE_KEY = "structure";
    public static final String CENTRE_POS_KEY = "centre_position";
    public static final String SPAWN_POSITIONS_KEY = "spawn_position";

    protected final StructureTemplate structure;
    protected final BlockPos centrePos;
    protected final ImmutableMap<Team, BlockPos> spawnPositions;

    protected final ServerWorld world;
    protected int tick;

    public AbstractGameMap(
            StructureTemplate structure,
            Set<BlockPos> spawnPositions,
            BlockPos centrePos,
            Set<Team> teams,
            ServerWorld world
    ) {
        this.structure = structure;
        this.spawnPositions = mapFromCollections(teams, spawnPositions);
        this.centrePos = centrePos.toImmutable();
        this.world = world;
    }

    public abstract void tick();

    public final void spreadPlayers(Multimap<Team, ServerPlayerEntity> teams) {
        teams.forEach((team, player) -> {
            BlockPos pos = this.spawnPositions.get(team);

            player.setPosition(pos.toCenterPos());
        });
    }

    public NbtCompound toNbt(NbtCompound compound) {
        compound.put(STRUCTURE_KEY, this.structure.writeNbt(new NbtCompound()));
        putBlockPosSet(compound, SPAWN_POSITIONS_KEY, this.spawnPositions.values());
        compound.putLong(CENTRE_POS_KEY, this.centrePos.asLong());

        return compound;
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
    public static File[] getMaps(String fileExtension) {
        final var files = getMapDirectory().listFiles((file) -> {
            final String[] splitName = file.getName().split("\\.");
            final String extension = splitName[splitName.length - 1];

            return Objects.equals(extension, fileExtension);
        });

        return (files != null) ? files : new File[0];
    }
    public static File getRandomMap(String fileExtension, World world, @Nullable String preferred_map) {
        File[] maps = getMaps(fileExtension);
        return maps[world.random.nextBetween(0, maps.length)];
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
