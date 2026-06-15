package com.soc.database.stats;

import java.util.UUID;

public class DuelsTable extends CombatTable {
	@Override
	public void win() {
		this.wins++;
		this.addXp(100);
	}

	@Override
	public void dealDamage(int damage) {
		this.damageDealt += damage;
		this.addXp(damage * 5L);
	}

	public void openChest(int tier) {
		if (tier == 2) this.addXp(20); //check this
	}

	public DuelsTable(UUID player) {
		super(player);
	}

	public DuelsTable() {
		super(null);
	}

	@Override
	public String getTableName() {
		return "DUELS";
	}
}
