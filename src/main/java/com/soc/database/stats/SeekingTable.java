package com.soc.database.stats;

import com.soc.game.manager.AbstractHidingGameManager;
import net.minecraft.util.DyeColor;

import java.util.UUID;

import static com.soc.game.map.AbstractHidingGameMap.HIDER_COLOUR;
import static com.soc.game.map.AbstractHidingGameMap.SEEKER_COLOUR;

public abstract class SeekingTable extends BaseGameTable {
    protected int seekerWins = 0;
    public void winAsSeeker() {
        this.wins++;
        this.seekerWins++;
    }
    protected int hiderWins = 0;
    public void winAsHider() {
        this.wins++;
        this.hiderWins++;
    }

    public void win(AbstractHidingGameManager<?, ?, ?> manager) {
        final DyeColor playerColour = manager.getTeam(this.player);

        if (playerColour == SEEKER_COLOUR) this.winAsSeeker();
        if (playerColour == HIDER_COLOUR) this.winAsHider();
    }

    protected int playersFound = 0;
    public void findPlayer() {
        this.playersFound++;
    }
    protected int timesFound = 0;
    public void find() {
        this.timesFound++;
    }
    protected int seekerKills = 0;
    public void killSeeker() {
        this.seekerKills++;
    }

    public SeekingTable(UUID player) {
        super(player);
    }

    public SeekingTable() {
        this(null);
    }
}
