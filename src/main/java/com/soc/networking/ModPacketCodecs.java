package com.soc.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public interface ModPacketCodecs {
    PacketCodec<ByteBuf, UUID> UUID = PacketCodecs.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);
    PacketCodec<ByteBuf, RegistryKey<World>> WORLD_KEY = RegistryKey.createPacketCodec(RegistryKeys.WORLD);
    PacketCodec<ByteBuf, BlockPos> BLOCK_POS = PacketCodecs.LONG.xmap(BlockPos::fromLong, BlockPos::asLong);
    PacketCodec<ByteBuf, BlockState> BLOCK_STATE = PacketCodecs.entryOf(Block.STATE_IDS);
    PacketCodec<ByteBuf, Box> BOX = PacketCodec.tuple(
            Vec3d.PACKET_CODEC, Box::getMinPos,
            Vec3d.PACKET_CODEC, Box::getMaxPos,
            Box::new
    );

//    PacketCodec<ByteBuf, BlockState> BLOCK_STATE = PacketCodecs.NBT_COMPOUND.xmap(nbt -> {
//        return NbtReadView.create(ErrorReporter.EMPTY, null, nbt).read("block_state", BlockState.CODEC).orElse(null);
//    }, blockState -> {
//        final NbtWriteView writeView = NbtWriteView.create(ErrorReporter.EMPTY, null);
//        writeView.put("block_state", BlockState.CODEC, blockState);
//
//        return writeView.getNbt();
//    });
//This is all bad and cursed I don't know why I wrote this and I don't even think it'll work
}
