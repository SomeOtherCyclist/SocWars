package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.game.manager.GameType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public record OpenQueueScreenPayload(Collection<GameType> queues, boolean allowsMultiQueue) implements CustomPayload {
    public static final Identifier OPEN_QUEUE_SCREEN_ID = Identifier.of(SocWars.MOD_ID, "open_queue_screen");
    public static final Id<OpenQueueScreenPayload> ID = new Id<>(OPEN_QUEUE_SCREEN_ID);
    public static final PacketCodec<RegistryByteBuf, OpenQueueScreenPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, GameType.PACKET_CODEC), OpenQueueScreenPayload::queues,
            PacketCodecs.BOOLEAN, OpenQueueScreenPayload::allowsMultiQueue,
            OpenQueueScreenPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
