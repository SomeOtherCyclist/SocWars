package com.soc.database.stats;

import java.util.UUID;

public class DuelsTable extends CombatTable {
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
