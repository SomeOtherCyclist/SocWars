package com.soc.networking.c2s;

import com.soc.SocWars;
import com.soc.networking.HoldsBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MapBlockSaveMapPayload(long pos) implements CustomPayload, HoldsBlockEntity {
    public static final Identifier MAP_BLOCK_SAVE_MAP_ID = Identifier.of(SocWars.MOD_ID, "map_block_save_map");
    public static final Id<MapBlockSaveMapPayload> ID = new Id<>(MAP_BLOCK_SAVE_MAP_ID);
    public static final PacketCodec<RegistryByteBuf, MapBlockSaveMapPayload> CODEC = PacketCodec.tuple(PacketCodecs.LONG, MapBlockSaveMapPayload::pos, MapBlockSaveMapPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public BlockEntity getBlockEntity(ServerPlayNetworking.Context context) {
        return this.getBlockEntity(context, this.pos);
    }
}
