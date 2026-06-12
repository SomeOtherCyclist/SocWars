package com.soc.blocks.blockentities;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import static com.soc.blocks.blockentities.ModBlockEntities.DISPLAY_BLOCK_ENTITY;

public class DisplayBlockEntity extends BlockEntity {
    private ItemStack displayItem;
    private Direction direction;
    private int rotation;

    public DisplayBlockEntity(BlockPos pos, BlockState state) {
        super(DISPLAY_BLOCK_ENTITY, pos, state);
        this.displayItem = ItemStack.EMPTY;
        this.direction = Direction.DOWN;
        this.rotation = 0;
    }

    public static void initialise() {}

    @Override
    protected void writeData(WriteView view) {
        if (this.displayItem != null && !this.displayItem.isEmpty()) view.put("display_item", ItemStack.CODEC, this.displayItem);
        if (this.direction != null) view.put("direction", Direction.CODEC, this.direction);
        view.put("rotation", Codec.INT, this.rotation);
    }

    @Override
    protected void readData(ReadView view) {
        this.displayItem = view.read("display_item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.direction = view.read("direction", Direction.CODEC).orElse(Direction.DOWN);
        this.rotation = view.read("rotation", Codec.INT).orElse(0);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem.copy();
        this.markDirty();
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public void changeFace() {
        this.direction = switch (this.direction) {
            case DOWN -> Direction.UP;
            case UP -> Direction.NORTH;
            case NORTH -> Direction.WEST;
            case SOUTH -> Direction.EAST;
            case WEST -> Direction.SOUTH;
            case EAST -> Direction.DOWN;
        };
        this.markDirty();
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void changeDirection() {
        this.rotation = (this.rotation + 1) % 4;
        this.markDirty();
    }

    public int getRotation() {
        return this.rotation;
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.getWorld() instanceof ServerWorld serverWorld) serverWorld.getChunkManager().markForUpdate(this.getPos());
    }
}
