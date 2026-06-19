package com.soc.networking.c2s;

import com.soc.SocWars;
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

import java.util.ArrayList;
import java.util.List;

public record KitSelectionPayload(BlockLocation block, List<GameType> selectedGameTypes, List<GameType> removedGameTypes) implements CustomPayload, HoldsBlockEntity {
    public static final Identifier KIT_SELECTION_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "kit_selection");
    public static final Id<KitSelectionPayload> ID = new Id<>(KIT_SELECTION_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, KitSelectionPayload> CODEC = PacketCodec.tuple(
            BlockLocation.PACKET_CODEC, KitSelectionPayload::block,
            PacketCodecs.collection(ArrayList::new, GameType.PACKET_CODEC), KitSelectionPayload::selectedGameTypes,
            PacketCodecs.collection(ArrayList::new, GameType.PACKET_CODEC), KitSelectionPayload::removedGameTypes,
            KitSelectionPayload::new
    );

    public static KitSelectionPayload select(BlockLocation block, List<GameType> selectedGameTypes) {
        return new KitSelectionPayload(block, selectedGameTypes, List.of());
    }

    public static KitSelectionPayload remove(BlockLocation block, List<GameType> removedGameTypes) {
        return new KitSelectionPayload(block, List.of(), removedGameTypes);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public BlockEntity getBlockEntity(ServerPlayNetworking.Context context) {
        return this.getBlockEntity(context, this.block);
    }
}
