package com.soc.player;

import com.soc.items.util.StatArmourBonus;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashSet;

public class PlayerData {
    public static final PacketCodec<ByteBuf, PlayerData> PACKET_CODEC = new PacketCodec<ByteBuf, PlayerData>() {
        public HashSet<RegistryEntry<Item>> decodeCollectibles(ByteBuf byteBuf) {
            final int numCollectibles = byteBuf.readInt();
            final HashSet<RegistryEntry<Item>> collectibles = new HashSet<>();

            for (int i = 0; i < numCollectibles; i++) {
                collectibles.add(Item.ENTRY_PACKET_CODEC.decode((RegistryByteBuf) byteBuf));
            }

            return collectibles;
        }

        public PlayerData decode(ByteBuf byteBuf) {
            final HashSet<RegistryEntry<Item>> collectibles = decodeCollectibles(byteBuf);

            return new PlayerData(collectibles);
        }

        public void encodeCollectibles(ByteBuf byteBuf, PlayerData playerData) {
            byteBuf.writeInt(playerData.collectibles.size());

            playerData.collectibles.forEach(collectible -> Item.ENTRY_PACKET_CODEC.encode((RegistryByteBuf) byteBuf, collectible));
        }

        public void encode(ByteBuf byteBuf, PlayerData playerData) {
            this.encodeCollectibles(byteBuf, playerData);
        }
    };

    //public int lives;
    public boolean invisible;

    private final HashSet<RegistryEntry<Item>> collectibles;
    public final StatArmourBonus steadfastBonus = new StatArmourBonus(EntityAttributes.KNOCKBACK_RESISTANCE);

    public PlayerData() {
        this.collectibles = new HashSet<>();
    }

    public PlayerData(HashSet<RegistryEntry<Item>> collectibles) {
        this.collectibles = collectibles;
    }

    public boolean collectCollectible(RegistryEntry<Item> collectible) {
        return collectibles.add(collectible);
    }
    public boolean hasCollectible(RegistryEntry<Item> collectible) {
        return collectibles.contains(collectible);
    }
    public HashSet<RegistryEntry<Item>> getCollectibles() {
        return this.collectibles;
    }
}
