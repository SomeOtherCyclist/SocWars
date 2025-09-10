package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.soc.game.map.BedwarsGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.Stack;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;

public class SkywarsGameManager extends AbstractGameManager {
    protected SkywarsGameManager(BedwarsGameMap map, World world, Set<Team> teams, Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules, int gameId) {
        super(map, world, teams, players, spreadRules, gameId);
    }

    @Override
    protected SkywarsGameMap getMap() {
        return (SkywarsGameMap) super.map;
    }

    @Override
    public ImmutableMultimap<Team, ServerPlayerEntity> assignTeams(Set<Team> teams, Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules) {
        final Stack<ServerPlayerEntity> playerStack = getRandomPlayerStack(players);


        return null;
    }

    @Override
    protected EventQueue buildEventQueue() {
        return null;
    }

    @Override
    public void startGame() {

    }

    @Override
    public void endGame() {

    }

    @Override
    public void onPlayerDeath() {

    }
}
