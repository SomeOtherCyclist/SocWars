package com.soc.game.manager;

import com.mojang.serialization.Codec;
import com.soc.game.map.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public enum GameType implements StringIdentifiable {
    SKYWARS(2, 8, "skywars", SkywarsGameMap.FILE_EXTENSION, SkywarsGameMap.MAP_FIELDS),
    BEDWARS(2, 16, "bedwars", BedwarsGameMap.FILE_EXTENSION, BedwarsGameMap.MAP_FIELDS),
    PROP_HUNT(2, 8, "prop_hunt", PropHuntGameMap.FILE_EXTENSION, PropHuntGameMap.MAP_FIELDS),
    HIDE_AND_SEEK(2, 8, "hide_and_seek", HideAndSeekGameMap.FILE_EXTENSION, HideAndSeekGameMap.MAP_FIELDS),
    DUELS(2, 2, "duels", DuelsGameMap.FILE_EXTENSION, DuelsGameMap.MAP_FIELDS);

    public static final PacketCodec<RegistryByteBuf, GameType> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, GameType::ordinal, GameType::fromOrdinal);
    public static final Codec<GameType> CODEC = StringIdentifiable.createCodec(GameType::values);

    private final int minPlayers;
    private final int maxPlayers;
    private final String name;
    private final String fileExtension;
    private final Map<String, RangedIntField> mapFields;

    GameType(int minPlayers, int maxPlayers, String name, String fileExtension, Map<String, RangedIntField> mapFields) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.name = name;
        this.fileExtension = fileExtension;
        this.mapFields = mapFields;
    }

    public int minPlayers() {
        return this.minPlayers;
    }

    public int maxPlayers() {
        return this.maxPlayers;
    }

    public MutableText getVariantName() {
        return Text.translatable("game_type." + this.name);
    }

    public MutableText getCompactVariantName() {
        return Text.translatable("game_type." + this.name + ".compact");
    }

    public static GameType fromOrdinal(int ordinal) {
        final GameType[] values = GameType.values();
        return values[ordinal < values.length ? ordinal : 0];
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Map<String, RangedIntField> getMapFields() {
        return this.mapFields;
    }
}