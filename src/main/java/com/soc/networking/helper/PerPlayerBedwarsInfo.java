package com.soc.networking.helper;

import com.soc.game.manager.bedwars.PlayerStats;
import com.soc.networking.ModPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.UUID;

public final class PerPlayerBedwarsInfo {
    public static final PacketCodec<RegistryByteBuf, PerPlayerBedwarsInfo> PACKET_CODEC = PacketCodec.tuple(
            ModPacketCodecs.UUID, PerPlayerBedwarsInfo::player,
            PacketCodecs.BOOLEAN, PerPlayerBedwarsInfo::isAlive,
            PerPlayerBedwarsInfo::new
    );
    private final UUID player;
    private boolean isAlive;

    public PerPlayerBedwarsInfo(UUID player, boolean isAlive) {
        this.player = player;
        this.isAlive = isAlive;
    }

    public PerPlayerBedwarsInfo(PlayerStats stats) {
        this(
                stats.getPlayer(),
                stats.isAlive()
        );
    }

    public UUID player() {
        return this.player;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public void eliminate() {
        this.isAlive = false;
    }
}
