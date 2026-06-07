package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public record BatchParticlePayload(ParticleEffect particleType, List<Vec3d> positions, List<Vec3d> velocities) implements CustomPayload {
    public static final Identifier BATCH_PARTICLE_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "batch_particle");
    public static final Id<BatchParticlePayload> ID = new Id<>(BATCH_PARTICLE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, BatchParticlePayload> CODEC = PacketCodec.tuple(
            ParticleTypes.PACKET_CODEC, BatchParticlePayload::particleType,
            PacketCodecs.collection(ArrayList::new, Vec3d.PACKET_CODEC), BatchParticlePayload::positions,
            PacketCodecs.collection(ArrayList::new, Vec3d.PACKET_CODEC), BatchParticlePayload::velocities,
            BatchParticlePayload::new
    );

    public BatchParticlePayload(ParticleEffect particleType, List<Vec3d> positions, Vec3d velocity) {
        this(particleType, positions, Collections.nCopies(positions.size(), velocity));
    }

    public BatchParticlePayload(ParticleEffect particleType, Vec3d position, List<Vec3d> velocities) {
        this(particleType, Collections.nCopies(velocities.size(), position), velocities);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
