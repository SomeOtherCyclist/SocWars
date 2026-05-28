package com.soc.networking.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.networking.ModPacketCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

public record BlockLocation(RegistryKey<World> world, BlockPos pos) {
    public static final PacketCodec<ByteBuf, BlockLocation> PACKET_CODEC = PacketCodec.tuple(
            ModPacketCodecs.WORLD_KEY, BlockLocation::world,
            ModPacketCodecs.BLOCK_POS, BlockLocation::pos,
            BlockLocation::new
    );

    public static final Codec<BlockLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            World.CODEC.fieldOf("world").orElse(World.OVERWORLD).forGetter(BlockLocation::world),
            BlockPos.CODEC.fieldOf("pos").orElse(BlockPos.ORIGIN).forGetter(BlockLocation::pos)
    ).apply(instance, BlockLocation::new));

    public static final Codec<Optional<BlockLocation>> OPTIONAL_CODEC = Codecs.optional(CODEC);

    public BlockLocation(BlockEntity blockEntity) {
        this(Objects.requireNonNull(blockEntity.getWorld()).getRegistryKey(), blockEntity.getPos());
    }
}
