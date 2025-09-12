package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.BedwarsGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.Stack;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;

public class SkywarsGameManager extends AbstractGameManager {
    protected SkywarsGameManager(ServerWorld world, Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules, int gameId) {
        super(world, players, spreadRules, gameId);
    }

    @Override
    protected SkywarsGameMap getMap() {
        return (SkywarsGameMap) super.map;
    }

    @Override
    protected AbstractGameMap buildMap() {
        return null;
    }

    @Override
    public ImmutableMultimap<Team, ServerPlayerEntity> buildTeams(Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules) {
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
