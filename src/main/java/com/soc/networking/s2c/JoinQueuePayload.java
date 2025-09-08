package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.player.PlayerData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record JoinQueuePayload(String queue) implements CustomPayload {
    public static final Identifier JOIN_QUEUE_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "join_queue");
    public static final Id<JoinQueuePayload> ID = new Id<>(JOIN_QUEUE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, JoinQueuePayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, JoinQueuePayload::queue, JoinQueuePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
