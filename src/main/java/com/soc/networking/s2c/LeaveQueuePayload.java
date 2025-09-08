package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LeaveQueuePayload(String queue) implements CustomPayload {
    public static final Identifier LEAVE_QUEUE_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "leave_queue");
    public static final Id<LeaveQueuePayload> ID = new Id<>(LEAVE_QUEUE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, LeaveQueuePayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, LeaveQueuePayload::queue, LeaveQueuePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
