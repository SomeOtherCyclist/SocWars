package com.soc.database.stats;

import java.util.UUID;

public abstract class BaseGameTable extends BaseTable {
    protected int wins = 0;
    public void win() { this.wins++; }
    protected int losses = 0;
    public void lose() { this.losses++; }
    protected long xp = 0;
    public void addXp(long xp) { this.xp += xp; }

	public long getXp() {
		return this.xp;
	}

	public int getDoubloons() {
		return (int)(this.xp / 25L);
	}

	protected BaseGameTable(UUID player) {
        super(player);
    }
}
