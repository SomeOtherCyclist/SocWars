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

public record BuyKitPayload(Kit kit, int cost, String scoreboardVariable) implements CustomPayload {
    public static final Identifier KIT_BLOCK_UPDATE_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "buy_kit");
    public static final Id<BuyKitPayload> ID = new Id<>(KIT_BLOCK_UPDATE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, BuyKitPayload> CODEC = PacketCodec.tuple(
            Kit.PACKET_CODEC, BuyKitPayload::kit,
            PacketCodecs.INTEGER, BuyKitPayload::cost,
            PacketCodecs.STRING, BuyKitPayload::scoreboardVariable,
            BuyKitPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
