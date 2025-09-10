package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.soc.game.map.BedwarsGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;

public class BedwarsGameManager extends AbstractGameManager {
    protected BedwarsGameManager(BedwarsGameMap map, World world, Collection<Team> teams, Collection<ServerPlayerEntity> players, @NotNull SpreadRules spreadRules, int gameId) {
        super(map, world, teams, players, spreadRules, gameId);
    }

    @Override
    protected BedwarsGameMap getMap() {
        return (BedwarsGameMap) super.map;
    }

    /*
    @Override
    protected ImmutableSet<Team> makeTeams() {
        ImmutableSet.Builder<Team> builder = ImmutableSet.builder();

        for (Formatting colour : new Formatting[]{
                Formatting.RED,
                Formatting.YELLOW,
                Formatting.GREEN,
                Formatting.BLUE
        }) builder.add(super.addTeamFromFormatting(colour));

        return builder.build();
    }
     */

    @Override
    public ImmutableMultimap<Team, ServerPlayerEntity> assignTeams(Collection<Team> teams, Collection<ServerPlayerEntity> players, SpreadRules spreadRules) {
        final Stack<ServerPlayerEntity> playerStack = getRandomPlayerStack(players);

        ImmutableMultimap.Builder<Team, ServerPlayerEntity> builder = ImmutableMultimap.builder();
        List<Team> teamList = teams.stream().toList();

        for (int i = 0; i < players.size(); i++) {
            builder.put(teamList.get(i % Math.min(spreadRules.numTeams(), teams.size())), playerStack.pop());
        }

        return builder.build();
    }

    @Override
    public void startGame() {

    }

    @Override
    public void onPlayerDeath() {

    }
}
