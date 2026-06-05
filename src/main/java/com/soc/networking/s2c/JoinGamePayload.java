package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record JoinGamePayload() implements CustomPayload {
    public static final Identifier JOIN_GAME_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "join_game");
    public static final Id<JoinGamePayload> ID = new Id<>(JOIN_GAME_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, JoinGamePayload> CODEC = PacketCodec.unit(new JoinGamePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
