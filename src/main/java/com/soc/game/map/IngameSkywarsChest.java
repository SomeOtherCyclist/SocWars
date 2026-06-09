package com.soc.game.map;

import com.soc.nbt.SkywarsChest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class IngameSkywarsChest {
    private final int tier;
    private final Direction facing;
    private final Set<UUID> playersWhoHaveOpened;

    public IngameSkywarsChest(int tier, Direction facing) {
        this.tier = tier;
        this.facing = facing;
        this.playersWhoHaveOpened = new HashSet<>(4, 0.8f);
    }

    public IngameSkywarsChest(SkywarsChest chest) {
        this(chest.tier(), chest.facing());
    }

    public int getTier() {
        return this.tier;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public boolean open(ServerPlayerEntity player) {
        return this.playersWhoHaveOpened.add(player.getUuid());
    }

    public int getFillOrdinal() {
        return this.playersWhoHaveOpened.size() - 1;
    }
}
