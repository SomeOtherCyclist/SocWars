package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.lib.SparseVoxelOctree;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public record BlockProtectionPayload(Optional<SparseVoxelOctree<Boolean>> blockProtectionOverlay, Optional<BlockPos> origin, int minHeight, int maxHeight) implements CustomPayload {
    public static final Identifier BLOCK_PROTECTION_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "block_protection");
    public static final Id<BlockProtectionPayload> ID = new Id<>(BLOCK_PROTECTION_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, BlockProtectionPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.optional(SparseVoxelOctree.packetCodec(PacketCodecs.BOOLEAN)), BlockProtectionPayload::blockProtectionOverlay,
            PacketCodecs.optional(BlockPos.PACKET_CODEC), BlockProtectionPayload::origin,
            PacketCodecs.INTEGER, BlockProtectionPayload::minHeight,
            PacketCodecs.INTEGER, BlockProtectionPayload::maxHeight,
            BlockProtectionPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}