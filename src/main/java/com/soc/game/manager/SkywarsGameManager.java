package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
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
    public ImmutableMultimap<DyeColor, ServerPlayerEntity> buildTeams(Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules) {
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
    public boolean onPlayerDeath(ServerPlayerEntity entity, DamageSource source, float amount) {
        return true;
    }
}
