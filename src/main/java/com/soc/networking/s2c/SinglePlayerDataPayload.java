package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.networking.ModPacketCodecs;
import com.soc.player.PlayerData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record SinglePlayerDataPayload(UUID uuid, PlayerData playerData) implements CustomPayload {
    public static final Identifier PLAYER_DATA_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "single_player_data");
    public static final Id<SinglePlayerDataPayload> ID = new Id<>(PLAYER_DATA_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SinglePlayerDataPayload> CODEC = PacketCodec.tuple(
            ModPacketCodecs.UUID, SinglePlayerDataPayload::uuid,
            PlayerData.PACKET_CODEC, SinglePlayerDataPayload::playerData,
            SinglePlayerDataPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
