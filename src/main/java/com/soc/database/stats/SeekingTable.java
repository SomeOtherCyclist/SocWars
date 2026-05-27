package com.soc.database.stats;

import java.util.UUID;

public abstract class SeekingTable extends BaseGameTable {
    protected int finds = 0;
    public void grantFind() {
        this.finds++;
    }
    protected int founds = 0;
    public void grantFound() {
        this.founds++;
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
