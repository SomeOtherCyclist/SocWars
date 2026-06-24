package com.soc.database.stats;

import java.util.UUID;

public class HideAndSeekTable extends AbstractHidingTable {
	@Override
	public void winAsSeeker() {
		this.wins++;
		this.seekerWins++;
		this.addXp(750);
	}
	@Override
	public void winAsHider() {
		this.wins++;
		this.hiderWins++;
		this.addXp(250);
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
		this.addXp(100);
	}

	public void surviveSecond() {
		this.secondsSurvived++;
		this.addXp(2);
	}

	public HideAndSeekTable(UUID player) {
		super(player);
	}

	public HideAndSeekTable() {
		super();
	}

	@Override
	public String getTableName() {
		return "HIDEANDSEEK";
	}
}
