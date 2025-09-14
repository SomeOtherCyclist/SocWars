package com.soc.networking.c2s;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record MapBlockUpdatePayload(long pos, RegistryKey<World> world, long regionSize, String mapName, int mapType) implements CustomPayload {
    public static final Identifier MAP_BLOCK_UPDATE_ID = Identifier.of(SocWars.MOD_ID, "map_block_update");
    public static final Id<MapBlockUpdatePayload> ID = new Id<>(MAP_BLOCK_UPDATE_ID);
    public static final PacketCodec<RegistryByteBuf, MapBlockUpdatePayload> CODEC = PacketCodec.tuple(PacketCodecs.LONG, MapBlockUpdatePayload::pos, RegistryKey.createPacketCodec(RegistryKeys.WORLD), MapBlockUpdatePayload::world, PacketCodecs.LONG, MapBlockUpdatePayload::regionSize, PacketCodecs.STRING, MapBlockUpdatePayload::mapName, PacketCodecs.INTEGER, MapBlockUpdatePayload::mapType, MapBlockUpdatePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
