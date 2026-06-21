package com.soc.player;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Vec3d;

public final class Illusion {
	public static final PacketCodec<ByteBuf, Illusion> PACKET_CODEC = PacketCodec.tuple(
			Vec3d.PACKET_CODEC, Illusion::getPos,
			PacketCodecs.LONG, Illusion::getExpiryTime,
			Illusion::new
	);

	private final Vec3d pos;
	private long expiryTime;

	public Illusion(Vec3d pos, long expiryTime) {
		this.pos = pos;
		this.expiryTime = expiryTime;
	}

	public Vec3d getPos() {
		return this.pos;
	}

	public long getExpiryTime() {
		return this.expiryTime;
	}

	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}

	public boolean isExpired(long worldTime) {
		return worldTime > this.expiryTime;
	}
}
