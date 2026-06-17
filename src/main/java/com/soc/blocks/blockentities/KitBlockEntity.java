package com.soc.blocks.blockentities;

import com.mojang.serialization.Codec;
import com.soc.game.Kit;
import com.soc.game.manager.GameType;
import com.soc.networking.c2s.KitBlockUpdatePayload;
import com.soc.player.PlayerDataManager;
import com.soc.screenhandler.KitBlockCreationScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static com.soc.blocks.blockentities.ModBlockEntities.KIT_BLOCK_ENTITY;

public class KitBlockEntity extends LockableContainerBlockEntity {
    private Kit kit;
    private Map<GameType, Boolean> allowedGameTypes;
    private String scoreboardVariableName;
    private int cost;

    public KitBlockEntity(BlockPos pos, BlockState state) {
        super(KIT_BLOCK_ENTITY, pos, state);
        this.kit = new Kit();
        this.allowedGameTypes = new LinkedHashMap<>();
        this.scoreboardVariableName = "";
        this.cost = 0;

        for (GameType gameType : GameType.values()) {
            this.allowedGameTypes.put(gameType, true);
        }
    }

    @Override
    protected void writeData(WriteView view) {
        view.put("kit", Kit.CODEC, this.kit);
        view.put("allowed_game_types", Codec.unboundedMap(GameType.CODEC, Codec.BOOL), this.allowedGameTypes);
        view.put("scoreboard_variable_name", Codecs.ESCAPED_STRING, this.scoreboardVariableName);
        view.put("cost", Codecs.POSITIVE_INT, this.cost);
    }

    @Override
    protected void readData(ReadView view) {
        this.kit = view.read("kit", Kit.CODEC).orElse(new Kit());
        this.allowedGameTypes = view.read("allowed_game_types", Codec.unboundedMap(GameType.CODEC, Codec.BOOL)).map(LinkedHashMap::new).orElse(new LinkedHashMap<>());
        this.scoreboardVariableName = view.read("scoreboard_variable_name", Codecs.ESCAPED_STRING).orElse("");
        this.cost = view.read("cost", Codecs.POSITIVE_INT).orElse(0);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.kit_block_creation");
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.kit.getHeldStacks();
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.kit.setHeldStacks(inventory);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new KitBlockCreationScreenHandler(syncId, playerInventory, this.kit, this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    private NbtCompound toPacketNbt(BlockEntity blockEntity, DynamicRegistryManager dynamicRegistryManager) {
        final NbtCompound nbt = this.createNbt(dynamicRegistryManager);
        nbt.putBoolean("valid_variable", this.world.getScoreboard().getObjectiveNames().contains(this.scoreboardVariableName));
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, this::toPacketNbt);
    }

    @Override
    public int size() {
        return this.kit.size();
    }

    public Kit getKit() {
        return this.kit;
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {}

    public void update(KitBlockUpdatePayload update) {
        this.allowedGameTypes = new LinkedHashMap<>(update.allowedGameTypes());

        if (this.getWorld() instanceof ServerWorld serverWorld && !this.kit.getName().equals(update.kit().getName()) && !update.kit().getName().equals(Kit.DEFAULT_NAME)) {
            PlayerDataManager.renameKit(serverWorld, this.kit.getName(), update.kit().getName());
        }

        this.kit = update.kit();

        this.markDirty();
    }

    public boolean allowsGameType(GameType gameType) {
        return this.allowedGameTypes.get(gameType);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.getWorld() instanceof ServerWorld serverWorld) serverWorld.getChunkManager().markForUpdate(this.getPos());
    }

    public List<GameType> getAllowedGameTypesList() {
        return this.allowedGameTypes.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).sorted(Comparator.comparingInt(GameType::ordinal)).toList();
    }

    public Map<GameType, Boolean> getAllowedGameTypes() {
        return this.allowedGameTypes;
    }

    public void setGameTypeAllowed(GameType gameType, Boolean isAllowed) {
        this.allowedGameTypes.put(gameType, isAllowed);
    }

    public static Text getKitSelectionMessage(List<GameType> gameTypes, Kit kit) {
        if (gameTypes.isEmpty()) {
            return Text.translatable("message.kit_selection.empty");
        } else {
            final MutableText message = Text.translatable("message.kit_selection", Text.literal(kit.getName()).formatted(Formatting.BOLD));
            for (GameType gameType : gameTypes) {
                message.append("\n  ").append(gameType.getVariantName().formatted(Formatting.GOLD));
            }
            return message;
        }
    }
}
