package com.soc.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.game.Kit;
import com.soc.game.manager.GameType;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.*;

import static com.soc.lib.SocWarsLib.ifNotNull;

public class PlayerData {
    //Maybe I should just replace this with a normal tuple codec. --> What is now the present me says yes that was a good idea it was much easier thank you.
    public static final PacketCodec<ByteBuf, PlayerData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, PacketCodecs.BOOLEAN), PlayerData::getCollectibles,
            PacketCodecs.optional(com.soc.networking.PacketCodecs.BLOCK_STATE), playerData -> Optional.ofNullable(playerData.morph),
            PlayerData::new
    );

    public static final PacketCodec<ByteBuf, PlayerData> ALL_SYNC_PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.optional(com.soc.networking.PacketCodecs.BLOCK_STATE), playerData -> Optional.ofNullable(playerData.morph),
            PlayerData::new
    );

    public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Codec.BOOL).fieldOf("collectibles").orElse(new ArrayList<>()).forGetter(PlayerData::getCollectibles),
            Codec.unboundedMap(GameType.CODEC, Kit.CODEC).fieldOf("equipped_kits").orElse(new HashMap<>()).forGetter(playerData -> playerData.equippedKits),
            Codecs.optional(BlockState.CODEC).fieldOf("morph").orElse(null).forGetter(playerData -> Optional.ofNullable(playerData.morph))
    ).apply(instance, PlayerData::new));

    private List<Boolean> collectibles;
    private Map<GameType, Kit> equippedKits;
    private BlockState morph;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public PlayerData(List<Boolean> collectibles, Map<GameType, Kit> equippedKits, Optional<BlockState> morph) {
        this.collectibles = new ArrayList<>(collectibles);
        this.equippedKits = new HashMap<>(equippedKits);
        this.morph = morph.orElse(null);
    }

    public PlayerData(List<Boolean> collectibles, Map<GameType, Kit> equippedKits) {
        this(collectibles, equippedKits, Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public PlayerData(List<Boolean> collectibles, Optional<BlockState> morph) {
        this(collectibles, new HashMap<>(), morph);
    }

    public PlayerData() {
        this(new ArrayList<>(), new HashMap<>());
    }

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public PlayerData(Optional<BlockState> morph) {
		this(new ArrayList<>(), new HashMap<>(), morph);
	}

	public boolean collectCollectible(int id) {
        if (id < 0) return false;

        while (id >= this.collectibles.size()) this.collectibles.add(false);

        return this.collectibles.set(id, true);
    }

    public void resetCollectible(int id) {
        if (id < 0 || id >= this.collectibles.size()) return;

        this.collectibles.set(id, false);
    }

    public boolean hasCollectible(int collectible) {
        return collectible >= 0 && collectible < this.collectibles.size() && this.collectibles.get(collectible);
    }

    public List<Boolean> getCollectibles() {
        return this.collectibles;
    }

    public void tryApplyKit(GameType gameType, ServerPlayerEntity player) {
        ifNotNull(this.equippedKits.get(gameType), kit -> kit.apply(player));
    }

    public void setKits(Kit kit, List<GameType> gameTypes) {
        for (GameType gameType : gameTypes) {
            this.equippedKits.put(gameType, kit);
        }
    }

    public void setMorph(World world, BlockState morph) {
        this.morph = morph;
		ifNotNull(world.getServer(), PlayerDataManager::sendDataToAll);
    }

    public BlockState getMorph() {
        return this.morph;
    }

	public void merge(PlayerData other) {
		if (!other.collectibles.isEmpty()) this.collectibles = other.collectibles;
		if (!other.equippedKits.isEmpty()) this.equippedKits = other.equippedKits;
		this.morph = other.morph;
	}
}
