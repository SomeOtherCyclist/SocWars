package com.soc.networking.c2s;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RequestOpenQueueScreenPayload() implements CustomPayload {
    public static final Identifier REQUEST_OPEN_QUEUE_SCREEN_ID = Identifier.of(SocWars.MOD_ID, "request_open_queue_screen");
    public static final Id<RequestOpenQueueScreenPayload> ID = new Id<>(REQUEST_OPEN_QUEUE_SCREEN_ID);
    public static final PacketCodec<RegistryByteBuf, RequestOpenQueueScreenPayload> CODEC = PacketCodec.unit(new RequestOpenQueueScreenPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
