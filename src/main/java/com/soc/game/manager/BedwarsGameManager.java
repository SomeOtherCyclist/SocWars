package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.soc.game.map.BedwarsGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;

public class BedwarsGameManager extends AbstractGameManager {
    protected BedwarsGameManager(ServerWorld world, Set<ServerPlayerEntity> players, @NotNull SpreadRules spreadRules, int gameId) {
        super(world, players, spreadRules, gameId);
    }

    @Override
    protected BedwarsGameMap getMap() {
        return (BedwarsGameMap) super.map;
    }

    @Override
    protected BedwarsGameMap buildMap() {
        return BedwarsGameMap.loadRandomMap(super.teams.keySet(), super.world).get();
    }

    @Override
    public ImmutableMultimap<Team, ServerPlayerEntity> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules) {
        final Stack<ServerPlayerEntity> playerStack = getRandomPlayerStack(players);
        final int numTeams = Math.min(spreadRules.numTeams(), teams.size());

        final ImmutableMultimap.Builder<Team, ServerPlayerEntity> builder = ImmutableMultimap.builder();

        final Set<Formatting> teamColours = (numTeams <= 4 ? FOUR_TEAMS_COLOURS : EIGHT_TEAMS_COLOURS);
        final ArrayList<Team> teamsUnlimited = addTeamsFromFormatting(teamColours);
        Collections.shuffle(teamsUnlimited);
        final List<Team> teams = teamsUnlimited.stream().limit(numTeams).toList();

        for (int i = 0; i < players.size(); i++) {
            builder.put(teams.get(i % numTeams), playerStack.pop());
        }

        return builder.build();
    }

    @Override
    protected EventQueue buildEventQueue() {
        final EventQueue queue = new EventQueue();

        queue.addEventSeconds(3, (manager) -> {}, "events.bedwars.diamond2");

        return queue;
    }

    @Override
    public void startGame() {
        super.startGame();
    }

    @Override
    public void endGame() {
        super.endGame();
    }

    @Override
    public void onPlayerDeath() {

    }
}
