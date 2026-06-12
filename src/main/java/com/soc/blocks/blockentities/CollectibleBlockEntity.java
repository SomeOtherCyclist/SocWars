package com.soc.blocks.blockentities;

import com.mojang.serialization.Codec;
import com.soc.player.CollectiblesManager;
import com.soc.player.PlayerData;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.soc.blocks.blockentities.ModBlockEntities.COLLECTIBLE_BLOCK_ENTITY;
import static com.soc.lib.SocWarsLib.randomCentredVec3d;
import static net.minecraft.block.SkullBlock.ROTATION;

public class CollectibleBlockEntity extends BlockEntity {
    private int id;
    final int rotation;

    public CollectibleBlockEntity(BlockPos pos, BlockState state) {
        super(COLLECTIBLE_BLOCK_ENTITY, pos, state);
        this.rotation = state.get(ROTATION);
    }

    public static void initialise() {}

    public float getRotation() {
        return this.rotation * -0.39269908169872414f + 3.141592653589793f; // -PI/8 + PI
    }

    public int getId() {
        return this.id;
    }

    @Override
    protected void writeData(WriteView view) {
        if (this.id != -1) view.put("tracking_id", Codec.INT, this.id);
    }

    @Override
    protected void readData(ReadView view) {
        this.id = view.read("tracking_id", Codec.INT).orElse(-1);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.id = CollectiblesManager.addCollectibleBlock(this);
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        CollectiblesManager.removeCollectibleBlock(this.id, (ServerWorld)this.world, pos);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.getWorld() instanceof ServerWorld serverWorld) serverWorld.getChunkManager().markForUpdate(this.getPos());
    }

    public static void clientTick(World world, BlockPos pos, BlockState blockState, CollectibleBlockEntity blockEntity) {} //Yeah yeah another mixin target method gleesh
}
