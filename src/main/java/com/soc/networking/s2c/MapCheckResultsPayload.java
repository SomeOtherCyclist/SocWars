package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.game.map.MapCheckResults;
import com.soc.player.PlayerData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/*
public record MapCheckResultsPayload() implements CustomPayload {
    public static final Identifier MAP_CHECK_RESULTS_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "map_check_results");
    public static final Id<MapCheckResultsPayload> ID = new Id<>(MAP_CHECK_RESULTS_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, MapCheckResultsPayload> CODEC = PacketCodec.tuple(MapCheckResultsPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
 */
