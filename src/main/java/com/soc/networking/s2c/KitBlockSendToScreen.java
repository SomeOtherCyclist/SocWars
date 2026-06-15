package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.networking.helper.BlockLocation;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record KitBlockSendToScreen(BlockLocation block) implements CustomPayload {
    public static final Identifier KIT_BLOCK_SEND_TO_SCREEN = Identifier.of(SocWars.MOD_ID, "kit_block_send_to_screen");
    public static final Id<KitBlockSendToScreen> ID = new Id<>(KIT_BLOCK_SEND_TO_SCREEN);
    public static final PacketCodec<RegistryByteBuf, KitBlockSendToScreen> CODEC = PacketCodec.tuple(
            BlockLocation.PACKET_CODEC, KitBlockSendToScreen::block,
            KitBlockSendToScreen::new
    );

    public KitBlockSendToScreen(KitBlockEntity blockEntity) {
        this(new BlockLocation(blockEntity));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
