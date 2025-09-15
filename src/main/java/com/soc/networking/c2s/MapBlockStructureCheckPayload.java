package com.soc.networking.c2s;

import com.soc.SocWars;
import com.soc.networking.HoldsBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record MapBlockStructureCheckPayload(long pos) implements CustomPayload, HoldsBlockEntity {
    public static final Identifier MAP_BLOCK_STRUCTURE_CHECK_ID = Identifier.of(SocWars.MOD_ID, "map_block_structure_check");
    public static final Id<MapBlockStructureCheckPayload> ID = new Id<>(MAP_BLOCK_STRUCTURE_CHECK_ID);
    public static final PacketCodec<RegistryByteBuf, MapBlockStructureCheckPayload> CODEC = PacketCodec.tuple(PacketCodecs.LONG, MapBlockStructureCheckPayload::pos, MapBlockStructureCheckPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public BlockEntity getBlockEntity(ServerPlayNetworking.Context context) {
        return this.getBlockEntity(context, this.pos);
    }
}
