package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.game.manager.GameType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record QueuePayload(List<GameType> queues) implements CustomPayload {
    public static final Identifier QUEUES_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "queues");
    public static final Id<QueuePayload> ID = new Id<>(QUEUES_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, QueuePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, GameType.PACKET_CODEC), QueuePayload::queues,
            QueuePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
