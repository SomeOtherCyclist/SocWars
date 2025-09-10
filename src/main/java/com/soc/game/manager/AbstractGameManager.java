package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public abstract class AbstractGameManager {
    protected final AbstractGameMap map;
    protected final World world;
    protected final ImmutableMultimap<Team, ServerPlayerEntity> teams;
    public final int gameId;

    protected AbstractGameManager(AbstractGameMap map, World world, Collection<Team> teams, Collection<ServerPlayerEntity> players, SpreadRules spreadRules, int gameId) {
        this.map = map;
        this.world = world;
        this.teams = this.assignTeams(teams, players, spreadRules);
        this.gameId = gameId;
    }

    protected abstract AbstractGameMap getMap();
    protected final Team addTeamFromFormatting(Formatting formatting) {
        final Team team = this.world.getScoreboard().addTeam(this.gameId + "_" + formatting.toString());
        team.setColor(formatting);
        team.setDisplayName(Text.of(StringUtils.capitalize(formatting.toString())));
        team.setFriendlyFireAllowed(false);
        team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS);

        return team;
    }
    public abstract ImmutableMultimap<Team, ServerPlayerEntity> assignTeams(Collection<Team> teams, Collection<ServerPlayerEntity> players, SpreadRules spreadRules);
    public abstract void startGame();
    public abstract void onPlayerDeath();
}
