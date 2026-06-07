package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.game.manager.Event;
import com.soc.game.manager.EventQueue;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record EventQueuePayload(List<Event.ClientDisplayEvent> events, long gameTime) implements CustomPayload {
    public static final Identifier EVENT_QUEUE_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "event_queue");
    public static final Id<EventQueuePayload> ID = new Id<>(EVENT_QUEUE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, EventQueuePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, Event.ClientDisplayEvent.PACKET_CODEC), EventQueuePayload::events,
            PacketCodecs.LONG, EventQueuePayload::gameTime,
            EventQueuePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public EventQueuePayload(EventQueue<?> eventQueue, long originTime) {
        this(eventQueue.getEvents().stream().map(Event::getDisplayCopy).toList(), originTime);
    }
}
