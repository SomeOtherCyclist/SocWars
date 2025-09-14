package com.soc.blocks.blockentities;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

import static com.soc.blocks.blockentities.ModBlockEntities.COLLECTIBLE_BLOCK_ENTITY;

public class CollectibleBlockEntity extends BlockEntity {
    private RegistryEntry<Item> collectible;
    private String displayUuid;

    public CollectibleBlockEntity(BlockPos pos, BlockState state) {
        super(COLLECTIBLE_BLOCK_ENTITY, pos, state);
    }

    public static void initialise() {}

    public void setCollectible(RegistryEntry<Item> collectible) {
        this.collectible = collectible;
        this.markDirty();
    }

    public RegistryEntry<Item> getCollectible() {
        return this.collectible;
    }

    public void setDisplayUuid(String displayUuid) {
        this.displayUuid = displayUuid;
    }

    public String getDisplayUuid() {
        return this.displayUuid;
    }

    @Override
    protected void writeData(WriteView view) {
        if (this.collectible != null) view.put("collectible", Item.ENTRY_CODEC, this.collectible);
        if (this.displayUuid != null) view.put("display", Codec.STRING, this.displayUuid);

        super.writeData(view);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        this.collectible = view.read("collectible", Item.ENTRY_CODEC).orElse(null);
        this.displayUuid = view.read("display", Codec.STRING).orElse(null);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        //????????? breaks block breaking is broken when it doesn't have an assigned item
        super.onBlockReplaced(pos, oldState);

        Entity display = this.getWorld().getEntity(UUID.fromString(this.displayUuid));
        if (display != null) display.remove(Entity.RemovalReason.KILLED);

    }
}
