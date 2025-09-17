package com.soc.game.map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.soc.SocWars;
import com.soc.util.Random;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static com.soc.lib.SocWarsLib.putBlockPosCollection;

public abstract class AbstractGameMap {
    public static final String STRUCTURE_KEY = "structure";
    public static final String CENTRE_POS_KEY = "centre_positions";
    public static final String SPAWN_POSITIONS_KEY = "spawn_positions";
    public static final String SPAWN_TEAMS_KEY = "spawn_teams";

    protected final StructureTemplate structure;
    protected final BlockPos centrePos;
    protected final BlockPos absoluteCentrePos;
    protected final ImmutableMap<DyeColor, BlockPos> spawnPositions;

    protected final ServerWorld world;
    protected int tick;

    public AbstractGameMap(
            StructureTemplate structure,
            @NotNull ImmutableMap<DyeColor, BlockPos> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull BlockPos absoluteCentrePos, //Not marked @NotNull since it can be null when saving the map to file
            @NotNull ServerWorld world //Not marked @NotNull since it can be null when saving the map to file
    ) {
        this.structure = structure;
        this.spawnPositions = ImmutableMap.copyOf(spawnPositions);
        this.centrePos = centrePos.toImmutable();
        this.absoluteCentrePos = absoluteCentrePos;
        this.world = world;
    }

    /// Constructor used only for saving the map to file
    public AbstractGameMap(
            StructureTemplate structure,
            @NotNull ImmutableMap<DyeColor, BlockPos> spawnPositions,
            @NotNull BlockPos centrePos
    ) {
        this.structure = structure;
        this.spawnPositions = ImmutableMap.copyOf(spawnPositions);
        this.centrePos = centrePos.toImmutable();
        this.absoluteCentrePos = null;
        this.world = null;
    }

    public abstract void tick();

    public final void spreadPlayers(Multimap<DyeColor, ServerPlayerEntity> teams) {
        teams.forEach((team, player) -> {
            final BlockPos rawPos = this.spawnPositions.get(team);
            if (rawPos == null) {
                player.sendMessage(Text.literal("Go yell at Liam for screwing up the spreadPlayers() method"));
            } else {
                final BlockPos pos = this.pos(rawPos);

                player.setPosition(pos.toCenterPos());
            }
        });
    }

    public NbtCompound toNbt(NbtCompound compound) {
        compound.put(STRUCTURE_KEY, this.structure.writeNbt(new NbtCompound()));
        putBlockPosCollection(compound, SPAWN_POSITIONS_KEY, this.spawnPositions.values());
        compound.putIntArray(SPAWN_TEAMS_KEY, this.spawnPositions.keySet().stream().mapToInt(Enum::ordinal).toArray()); //I love how everything is pass by value god I love it so much
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
        final File[] maps = getMaps(fileExtension);
        return maps[world.random.nextBetween(0, maps.length - 1)];
    }
    public static Stack<ServerPlayerEntity> getRandomPlayerStack(Collection<ServerPlayerEntity> players) {
        final Stack<ServerPlayerEntity> playerStack = new Stack<>();
        Collections.shuffle((ArrayList<?>) new ArrayList<>(players).clone());
        playerStack.addAll(players);

        return playerStack;
    }
    public final BlockPos pos(BlockPos pos) {
        return pos.add(this.absoluteCentrePos);
    }
    public final Set<DyeColor> getTeamColours() {
        return this.spawnPositions.keySet();
    }

    public final void placeMap() {
        this.structure.place(this.world, this.absoluteCentrePos.subtract(this.centrePos), this.absoluteCentrePos, new StructurePlacementData(), this.world.random, Block.NOTIFY_LISTENERS);
    }
}
