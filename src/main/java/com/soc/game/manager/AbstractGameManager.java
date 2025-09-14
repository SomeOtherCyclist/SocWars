package com.soc.game.manager;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.formattingColourFromDye;

public abstract class AbstractGameManager {
    public static final ImmutableSet<DyeColor> FOUR_TEAMS_COLOURS = ImmutableSet.copyOf(new DyeColor[]{DyeColor.RED, DyeColor.YELLOW, DyeColor.GREEN, DyeColor.LIGHT_BLUE});
    public static final ImmutableSet<DyeColor> EIGHT_TEAMS_COLOURS = ImmutableSet.copyOf(new DyeColor[]{DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.GREEN, DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA});

    protected final AbstractGameMap map;
    protected final ServerWorld world;
    protected final ImmutableMultimap<DyeColor, ServerPlayerEntity> teams;
    protected final ImmutableMap<DyeColor, Team> scoreboardTeams;
    protected final EventQueue eventQueue;

    private final int gameId;

    protected int time;

    protected AbstractGameManager(ServerWorld world, Set<ServerPlayerEntity> players, SpreadRules spreadRules, int gameId) {
        this.map = this.buildMap();
        this.world = world;
        this.teams = this.buildTeams(players, spreadRules);
        this.scoreboardTeams = this.buildScoreboardTeams();
        this.eventQueue = this.buildEventQueue();
        this.gameId = gameId;
    }

    protected abstract AbstractGameMap getMap();
    protected abstract AbstractGameMap buildMap();
    public abstract ImmutableMultimap<DyeColor, ServerPlayerEntity> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules);
    public final ImmutableMap<DyeColor, Team> buildScoreboardTeams() {
        ImmutableMap.Builder<DyeColor, Team> builder = ImmutableMap.builder();

        this.teams.keySet().forEach(colour -> builder.put(colour, addTeamFromColour(colour)));

        return builder.build();
    }
    protected abstract EventQueue buildEventQueue();

    public void startGame() {
        this.getMap().spreadPlayers(this.teams);
    }
    public void endGame() {
        this.removeTeams();
        GamesManager.endGame(this.gameId);
    }
    public abstract void onPlayerDeath();

    public void tick() {
        time++;
        this.updateEventQueue();
    }

    public final void removeTeams() {
        final Scoreboard scoreboard = world.getScoreboard();
        scoreboardTeams.values().forEach(scoreboard::removeTeam);
    }

    public final ImmutableCollection<ServerPlayerEntity> getPlayers() {
        return teams.values();
    }

    public final Team addTeamFromColour(DyeColor colour) {
        final Team team = this.world.getScoreboard().addTeam(this.gameId + "_" + colour.toString());
        team.setColor(formattingColourFromDye(colour));
        team.setDisplayName(Text.of(StringUtils.capitalize(colour.toString())));
        team.setFriendlyFireAllowed(false);
        team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS);

        return team;
    }

    public final ArrayList<Team> addTeamsFromColours(Set<DyeColor> colours) {
        final ArrayList<Team> teams = new ArrayList<>();
        colours.forEach(colour -> teams.add(this.addTeamFromColour(colour)));

        return teams;
    }

    private void updateEventQueue() {
        Collection<Pair<Consumer<AbstractGameManager>, String>> events = this.eventQueue.tryPopEvents(this.time);
        events.forEach(event -> {
            event.getLeft().accept(this);
        });
    }

    public final Collection<Text> getUpcomingEvents() {
        return this.eventQueue.peekEventsText(this.time);
    }

    public final int getGameId() {
        return this.gameId;
    }
}
