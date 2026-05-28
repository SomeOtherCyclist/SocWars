package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.networking.ModPacketCodecs;
import com.soc.player.PlayerData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record AllSyncPlayerDataPayload(Map<UUID, PlayerData> playerDataMap) implements CustomPayload {
    public static final Identifier PLAYER_DATA_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "all_sync_player_data");
    public static final Id<AllSyncPlayerDataPayload> ID = new Id<>(PLAYER_DATA_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, AllSyncPlayerDataPayload> CODEC = PacketCodec.tuple(
            net.minecraft.network.codec.PacketCodecs.map(HashMap::new, ModPacketCodecs.UUID, PlayerData.ALL_SYNC_PACKET_CODEC), AllSyncPlayerDataPayload::playerDataMap,
            AllSyncPlayerDataPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
