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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public abstract class AbstractGameManager {
    public static final ImmutableSet<Formatting> FOUR_TEAMS_COLOURS = ImmutableSet.copyOf(new Formatting[]{Formatting.RED, Formatting.YELLOW, Formatting.GREEN, Formatting.BLUE});
    public static final ImmutableSet<Formatting> EIGHT_TEAMS_COLOURS = ImmutableSet.copyOf(new Formatting[]{Formatting.RED, Formatting.GOLD, Formatting.YELLOW, Formatting.GREEN, Formatting.AQUA, Formatting.DARK_BLUE, Formatting.DARK_PURPLE, Formatting.LIGHT_PURPLE});

    protected final AbstractGameMap map;
    protected final World world;
    protected final ImmutableMultimap<Team, ServerPlayerEntity> teams;
    protected final EventQueue eventQueue;

    private final int gameId;

    protected int time;

    protected AbstractGameManager(AbstractGameMap map, World world, Set<Team> teams, Set<ServerPlayerEntity> players, SpreadRules spreadRules, int gameId) {
        this.map = map;
        this.world = world;
        this.teams = this.assignTeams(teams, players, spreadRules);
        this.eventQueue = this.buildEventQueue();
        this.gameId = gameId;
    }

    protected abstract AbstractGameMap getMap();
    public abstract ImmutableMultimap<Team, ServerPlayerEntity> assignTeams(Set<Team> teams, Set<ServerPlayerEntity> players, SpreadRules spreadRules);
    protected abstract EventQueue buildEventQueue();

    public abstract void startGame();
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

    protected final Team addTeamFromFormatting(Formatting formatting) {
        final Team team = this.world.getScoreboard().addTeam(this.gameId + "_" + formatting.toString());
        team.setColor(formatting);
        team.setDisplayName(Text.of(StringUtils.capitalize(formatting.toString())));
        team.setFriendlyFireAllowed(false);
        team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS);

        return team;
    }

    protected final Set<Team> addTeamsFromFormatting(Set<Formatting> formattings) {
        ImmutableSet.Builder<Team> builder = ImmutableSet.builder();
        formattings.forEach(formatting -> builder.add(this.addTeamFromFormatting(formatting)));

        return builder.build();
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
