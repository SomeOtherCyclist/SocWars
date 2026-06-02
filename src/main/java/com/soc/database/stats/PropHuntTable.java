package com.soc.database.stats;

import java.util.UUID;

public class PropHuntTable extends SeekingTable {
	@Override
	public void winAsSeeker() {
		this.wins++;
		this.seekerWins++;
		this.addXp(1000);
	}
	@Override
	public void winAsHider() {
		this.wins++;
		this.hiderWins++;
		this.addXp(300);
	}

	@Override
	public void lose() {
		this.losses++;
		this.addXp(10);
	}

	@Override
	public void killSeeker() {
		this.seekerKills++;
		this.addXp(5000);
	}
	@Override
	public void findPlayer() {
		this.seekerKills++;
		this.addXp(200);
	}

	public void hitPlayer() {
		this.addXp(10);
	}

	public PropHuntTable(UUID player) {
		super(player);
	}

	public PropHuntTable() {
		super();
	}

	@Override
	public String getTableName() {
		return "PROPHUNT";
	}
}
