package com.soc.game.manager;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.StringUtils;

public enum GameType implements QueueProgress, StringIdentifiable {
    SKYWARS(2, 8, "skywars"),
    BEDWARS(1, 4, "bedwars"),
    PROP_HUNT(2, 8, "prop_hunt");

    private final int minPlayers;
    private final int maxPlayers;
    private final String name;

    GameType(int minPlayers, int maxPlayers, String name) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.name = name;
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

    public Text getVariantName() {
        return Text.translatable("game_type." + this.toString().toLowerCase());
    }

    public static GameType fromOrdinal(int ordinal) {
        final GameType[] values = GameType.values();
        return values[ordinal < values.length ? ordinal : 0];
    }

    public String getFileExtension() {
        return switch (this) {
            case SKYWARS -> "swmap";
            case BEDWARS -> "bwmap";
            case PROP_HUNT -> "phmap";
        };
    }

    @Override
    public String asString() {
        return this.name;
    }
}