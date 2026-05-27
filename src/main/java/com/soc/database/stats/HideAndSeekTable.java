package com.soc.database.stats;

import java.util.UUID;

public class HideAndSeekTable extends SeekingTable {
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
