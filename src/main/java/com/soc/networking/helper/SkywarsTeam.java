package com.soc.networking.helper;

import com.soc.networking.ModPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.UUID;
import java.util.stream.Stream;

public class SkywarsTeam implements GameTeam {
    public static final PacketCodec<RegistryByteBuf, SkywarsTeam> PACKET_CODEC = PacketCodec.tuple(
            ModPacketCodecs.UUID, SkywarsTeam::getPlayer,
            PacketCodecs.INTEGER, team -> team.lives,
            SkywarsTeam::new
    );

    private final UUID player;
    private int lives;

    public SkywarsTeam(UUID player, int lives) {
        this.player = player;
        this.lives = lives;
    }

    @Override
    public boolean isAlive() {
        return this.lives > 0;
    }

    public void eliminate() {
        this.lives = 0;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getLives() {
        return this.lives;
    }

    public UUID getPlayer() {
        return player;
    }

    @Override
    public Stream<UUID> getPlayersStream() {
        return Stream.of(this.player);
    }
}
