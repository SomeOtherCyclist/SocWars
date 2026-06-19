package com.soc.lib;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;

public class ScoreboardHelper {
	public static boolean incrementVariable(PlayerEntity player, String objectiveName, int value) {
		final ScoreboardObjective objective = player.getScoreboard().getNullableObjective(objectiveName);
		if (objective == null) return false;

		player.getScoreboard().getOrCreateScore(ScoreHolder.fromProfile(player.getGameProfile()), objective).incrementScore(value);
		return true;
	}

	public static boolean collectDoubloons(PlayerEntity player, int doubloons) {
		return incrementVariable(player, "Doubloons", doubloons);
	}

	public static boolean scoreboardVariableIsGreater(PlayerEntity player, String objectiveName, int minValue) {
		final ScoreboardObjective objective = player.getScoreboard().getNullableObjective(objectiveName);
		if (objective == null) return false;

		ReadableScoreboardScore score = player.getScoreboard().getScore(ScoreHolder.fromProfile(player.getGameProfile()), objective);
		if (score == null) return false;

		return score.getScore() >= minValue;
	}
}
