package com.soc.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.game.Kit;
import com.soc.game.manager.GameType;
import com.soc.networking.c2s.KitSelectionPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.*;

import static com.soc.lib.SocWarsLib.ifNotNull;

public class PlayerData {
    //Maybe I should just replace this with a normal tuple codec. --> What is now the present me says yes that was a good idea it was much easier thank you.
    public static final PacketCodec<ByteBuf, PlayerData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, PacketCodecs.BOOLEAN), PlayerData::getCollectibles,
            PacketCodecs.collection(HashSet::new, PacketCodecs.STRING), playerData -> playerData.ownedKits,
            PacketCodecs.optional(Morph.PACKET_CODEC), playerData -> Optional.ofNullable(playerData.morph),
            PlayerData::new
    );

    public static final PacketCodec<ByteBuf, PlayerData> ALL_SYNC_PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.optional(Morph.PACKET_CODEC), playerData -> Optional.ofNullable(playerData.morph),
            PacketCodecs.map(HashMap::newHashMap, EquipmentSlot.PACKET_CODEC, PacketCodecs.LONG), playerData -> playerData.illusions,
            PlayerData::new
    );

    public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Codec.BOOL).fieldOf("collectibles").orElse(new ArrayList<>()).forGetter(PlayerData::getCollectibles),
            Codec.unboundedMap(GameType.CODEC, Kit.CODEC).fieldOf("equipped_kits").orElse(new HashMap<>()).forGetter(playerData -> playerData.equippedKits),
            Codec.list(Codec.STRING).fieldOf("owned_kits").orElse(List.of()).forGetter(playerData -> List.copyOf(playerData.ownedKits)),
            Codecs.optional(Morph.CODEC).fieldOf("morph").orElse(null).forGetter(playerData -> Optional.ofNullable(playerData.morph))
    ).apply(instance, PlayerData::new));

    private List<Boolean> collectibles;
    private Map<GameType, Kit> equippedKits;
    private final Set<String> ownedKits;
    private Morph morph;
    private final Map<EquipmentSlot, Long> illusions;

    //Canonical constructor
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public PlayerData(List<Boolean> collectibles, Map<GameType, Kit> equippedKits, Set<String> ownedKits, Optional<Morph> morph, Map<EquipmentSlot, Long> illusions) {
        this.collectibles = new ArrayList<>(collectibles);
        this.equippedKits = new HashMap<>(equippedKits);
        this.ownedKits = ownedKits;
        this.morph = morph.orElse(null);
        this.illusions = illusions;
    }

    //Codec
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public PlayerData(List<Boolean> collectibles, Map<GameType, Kit> equippedKits, List<String> ownedKits, Optional<Morph> morph) {
        this(collectibles, equippedKits, new HashSet<>(ownedKits), morph, new HashMap<>());
    }

    //Packet codec
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public PlayerData(List<Boolean> collectibles, Set<String> ownedKits, Optional<Morph> morph) {
        this(collectibles, new HashMap<>(), ownedKits, morph, new HashMap<>());
    }

    //All sync packet codec
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public PlayerData(Optional<Morph> morph, Map<EquipmentSlot, Long> illusions) {
		this(new ArrayList<>(), new HashMap<>(), new HashSet<>(), morph, illusions);
	}

    //Empty
    public PlayerData() {
        this(new ArrayList<>(), new HashMap<>(), new HashSet<>(), Optional.empty(), new HashMap<>());
    }

    public void merge(PlayerData other) {
        if (!other.collectibles.isEmpty()) this.collectibles = other.collectibles;
        if (!other.equippedKits.isEmpty()) this.equippedKits = other.equippedKits;
        this.ownedKits.addAll(other.ownedKits);
        this.morph = other.morph;
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

    /// Returns {@code true} and applies the {@link Kit} if the player owns the kit. Will remove specified kits regardless of owning the kit.
    public boolean setKits(Kit kit, KitSelectionPayload payload) {
        for (GameType gameType : payload.removedGameTypes()) {
            this.equippedKits.remove(gameType);
        }

        if (!this.ownedKits.contains(kit.getName())) return false;

        for (GameType gameType : payload.selectedGameTypes()) {
            this.equippedKits.put(gameType, kit);
        }

        return true;
    }

    public boolean buyKit(ServerPlayerEntity player, String name) {
        if (name.equals(Kit.DEFAULT_NAME)) return false;

        final boolean kitIsNew = this.ownedKits.add(name);
        if (kitIsNew) PlayerDataManager.sendData(player);

        return kitIsNew;
    }

    public void renameKit(PlayerEntity player, String oldName, String newName) {
        if (this.ownedKits.remove(oldName)) {
            this.ownedKits.add(newName);

            if (player instanceof ServerPlayerEntity serverPlayer) PlayerDataManager.sendData(serverPlayer);
        }
    }

    public boolean ownsKit(String name) {
        return this.ownedKits.contains(name);
    }

    public void setMorph(World world, BlockState morph, PlayerEntity player) {
        this.morph = morph == null ? null : Morph.of(morph, world);
        if (this.morph == null) {
            Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MAX_HEALTH)).removeModifier(Morph.HEALTH_MODIFIER_ID);
        } else {
            Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MAX_HEALTH)).overwritePersistentModifier(new EntityAttributeModifier(Morph.HEALTH_MODIFIER_ID, this.morph.health() - 20f, EntityAttributeModifier.Operation.ADD_VALUE));
        }
        ifNotNull(world.getServer(), PlayerDataManager::sendDataToAll);
    }

    public Morph getMorph() {
        return this.morph;
    }

    public void addIllusion(World world, EquipmentSlot slot) {
        this.illusions.put(slot, world.getTime() + 3 * 20);

        ifNotNull(world.getServer(), PlayerDataManager::sendDataToAll);
    }

    public EquipmentSlot[] checkIllusions(long worldTime) {
        return this.illusions.entrySet().stream().map(entry -> {
            if (entry.getValue() <= worldTime) {
                this.illusions.remove(entry.getKey());
                return null;
            } else {
                return entry.getKey();
            }
        }).filter(Objects::nonNull).toArray(EquipmentSlot[]::new);

        //Shouldn't need to bother syncing this because it gets checked on all clients
    }
}
