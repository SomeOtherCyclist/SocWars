package com.soc.game.map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.soc.SocWars;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public abstract class AbstractGameMap {
    public static final String MAP_FILE_EXTENSION = "socmap";

    protected final StructureTemplate structure;

    protected final ImmutableSet<Team> teams;
    protected final ImmutableMap<Team, BlockPos> spawnLocations;

    protected final World world;
    protected final UUID gameId;
    protected int tick;

    protected AbstractGameMap(
            StructureTemplate structure,
            ImmutableSet<BlockPos> spawnLocations,
            World world
    ) {
        this.structure = structure;
        this.teams = this.makeTeams();
        this.spawnLocations = mapFromCollections(this.teams, spawnLocations);
        this.world = world;
        this.gameId = UUID.randomUUID();
    }

    public abstract void spreadPlayers(ArrayList<ServerPlayerEntity> players);
    public abstract void tick();
    public abstract ImmutableSet<Team> makeTeams();

    protected final Team addTeamFromFormatting(Formatting formatting) {
        final Team team = this.world.getScoreboard().addTeam(this.gameId.toString() + "_" + formatting.toString());
        team.setColor(formatting);
        team.setDisplayName(Text.of(StringUtils.capitalize(formatting.toString())));
        team.setFriendlyFireAllowed(false);
        team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS);

        return team;
    }
    
    protected static <T, U> ImmutableMap<T, U> mapFromCollections(Collection<T> t1, Collection<U> t2) {
        ImmutableMap.Builder<T, U> builder = ImmutableMap.builder();

        Iterator<T> t1Iterator = t1.iterator();
        Iterator<U> t2Iterator = t2.iterator();

        while (t1Iterator.hasNext() && t2Iterator.hasNext()) {
            builder.put(t1Iterator.next(), t2Iterator.next());
        }

        return builder.build();
    }

    public static File[] getMaps() {
        final var files = getMapDirectory().listFiles((file) -> {
            final String[] splitName = file.getName().split("\\.");
            final String extension = splitName[splitName.length - 1];

            return Objects.equals(extension, MAP_FILE_EXTENSION);
        });

        return (files != null) ? files : new File[0];
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

    public static Stack<ServerPlayerEntity> getRandomPlayerStack(ArrayList<ServerPlayerEntity> players) {
        final Stack<ServerPlayerEntity> playerStack = new Stack<>();
        Collections.shuffle((List<?>) players.clone());
        playerStack.addAll(players);

        return playerStack;
    }
}
