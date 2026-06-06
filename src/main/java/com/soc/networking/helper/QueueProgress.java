package com.soc.networking.helper;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record QueueProgress(int players, long completeTime, boolean allowSinglePlayer) {
	public static final PacketCodec<ByteBuf, QueueProgress> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, QueueProgress::players,
			PacketCodecs.LONG, QueueProgress::completeTime,
			PacketCodecs.BOOLEAN, QueueProgress::allowSinglePlayer,
			QueueProgress::new
	);
}