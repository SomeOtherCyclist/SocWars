package com.soc.database.stats;

import java.util.UUID;

public class PropHuntTable extends SeekingTable {
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
