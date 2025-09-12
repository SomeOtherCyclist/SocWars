package com.soc.game.manager;

import org.apache.commons.lang3.StringUtils;

public enum GameType implements QueueProgress {
    SKYWARS(2, 8),
    BEDWARS(2, 4),
    PROP_HUNT(2, 8);

    private final int minPlayers;
    private final int maxPlayers;

    GameType(int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public GameType fromNatural(String string) {
        return GameType.valueOf(string.replace(' ', '_').toUpperCase());
    }

    public String toNatural() {
        return StringUtils.capitalize(this.toString());
    }

    public int minPlayers() {
        return this.maxPlayers;
    }

    public int maxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public float getQueueProgress(int playerCount) {
        if (playerCount < this.minPlayers) return 0;

        float offset = (float) this.maxPlayers / (4 * this.minPlayers());
        float rawProgress = (playerCount - this.minPlayers + offset) / (this.maxPlayers - this.minPlayers + offset);
        return (float) Math.pow(rawProgress, 2.5f);
    }
}