package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.player.PlayerData;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayerDataPayload(PlayerData playerData) implements CustomPayload {
    public static final Identifier PLAYER_DATA_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "player_data");
    public static final Id<PlayerDataPayload> ID = new Id<>(PLAYER_DATA_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, PlayerDataPayload> CODEC = PacketCodec.tuple(PlayerData.PACKET_CODEC, PlayerDataPayload::playerData, PlayerDataPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
