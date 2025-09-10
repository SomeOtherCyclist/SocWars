package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.soc.game.map.BedwarsGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class SkywarsGameManager extends AbstractGameManager {
    protected SkywarsGameManager(BedwarsGameMap map, World world, Collection<Team> teams, Collection<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules, int gameId) {
        super(map, world, teams, players, spreadRules, gameId);
    }

    @Override
    protected SkywarsGameMap getMap() {
        return (SkywarsGameMap) super.map;
    }

    @Override
    public ImmutableMultimap<Team, ServerPlayerEntity> assignTeams(Collection<Team> teams, Collection<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules) {
        return null;
    }

    @Override
    public void startGame() {

    }

    @Override
    public void onPlayerDeath() {

    }
}
