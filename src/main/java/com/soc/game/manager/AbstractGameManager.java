package com.soc.game.manager;

import com.google.common.collect.ImmutableCollection;
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
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public abstract class AbstractGameManager {
    public static final ImmutableSet<Formatting> FOUR_TEAMS_COLOURS = ImmutableSet.copyOf(new Formatting[]{Formatting.RED, Formatting.YELLOW, Formatting.GREEN, Formatting.BLUE});
    public static final ImmutableSet<Formatting> EIGHT_TEAMS_COLOURS = ImmutableSet.copyOf(new Formatting[]{Formatting.RED, Formatting.GOLD, Formatting.YELLOW, Formatting.GREEN, Formatting.AQUA, Formatting.DARK_BLUE, Formatting.DARK_PURPLE, Formatting.LIGHT_PURPLE});

    protected final AbstractGameMap map;
    protected final ServerWorld world;
    protected final ImmutableMultimap<Team, ServerPlayerEntity> teams;
    protected final EventQueue eventQueue;

    private final int gameId;

    protected int time;

    protected AbstractGameManager(ServerWorld world, Set<ServerPlayerEntity> players, SpreadRules spreadRules, int gameId) {
        this.map = this.buildMap();
        this.world = world;
        this.teams = this.buildTeams(players, spreadRules);
        this.eventQueue = this.buildEventQueue();
        this.gameId = gameId;
    }

    protected abstract AbstractGameMap getMap();
    protected abstract AbstractGameMap buildMap();
    public abstract ImmutableMultimap<Team, ServerPlayerEntity> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules);
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
        teams.keySet().forEach(scoreboard::removeTeam);
    }

    public final ImmutableCollection<ServerPlayerEntity> getPlayers() {
        return teams.values();
    }

    public final Team addTeamFromFormatting(Formatting formatting) {
        final Team team = this.world.getScoreboard().addTeam(this.gameId + "_" + formatting.toString());
        team.setColor(formatting);
        team.setDisplayName(Text.of(StringUtils.capitalize(formatting.toString())));
        team.setFriendlyFireAllowed(false);
        team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS);

        return team;
    }

    public final ArrayList<Team> addTeamsFromFormatting(Set<Formatting> formattings) {
        final ArrayList<Team> teams = new ArrayList<>();
        formattings.forEach(formatting -> teams.add(this.addTeamFromFormatting(formatting)));

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
