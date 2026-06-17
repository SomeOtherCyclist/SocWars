package com.soc.networking.c2s;

import com.soc.SocWars;
import com.soc.game.Kit;
import com.soc.game.manager.GameType;
import com.soc.networking.HoldsBlockEntity;
import com.soc.networking.helper.BlockLocation;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public record KitBlockUpdatePayload(BlockLocation block, Map<GameType, Boolean> allowedGameTypes, Kit kit) implements CustomPayload, HoldsBlockEntity {
    public static final Identifier KIT_BLOCK_UPDATE_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "kit_block_update");
    public static final Id<KitBlockUpdatePayload> ID = new Id<>(KIT_BLOCK_UPDATE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, KitBlockUpdatePayload> CODEC = PacketCodec.tuple(
            BlockLocation.PACKET_CODEC, KitBlockUpdatePayload::block,
            PacketCodecs.map(LinkedHashMap::new, GameType.PACKET_CODEC, PacketCodecs.BOOLEAN), KitBlockUpdatePayload::allowedGameTypes,
            Kit.PACKET_CODEC, KitBlockUpdatePayload::kit,
            KitBlockUpdatePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public BlockEntity getBlockEntity(ServerPlayNetworking.Context context) {
        return this.getBlockEntity(context, this.block);
    }
}
