package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.game.manager.GameType;
import com.soc.items.DiceOfFate;
import com.soc.networking.helper.QueueProgress;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public record QueueProgressPayload(Map<GameType, QueueProgress> progressMap) implements CustomPayload {
	public static final Identifier QUEUE_PROGRESS_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "queue_progress");
	public static final Id<QueueProgressPayload> ID = new Id<>(QUEUE_PROGRESS_PAYLOAD_ID);
	public static final PacketCodec<RegistryByteBuf, QueueProgressPayload> CODEC = PacketCodec.tuple(
			PacketCodecs.map(HashMap::newHashMap, GameType.PACKET_CODEC, QueueProgress.PACKET_CODEC), QueueProgressPayload::progressMap,
			QueueProgressPayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
