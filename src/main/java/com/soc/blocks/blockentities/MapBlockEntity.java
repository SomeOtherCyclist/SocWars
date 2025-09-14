package com.soc.blocks.blockentities;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import static com.soc.blocks.blockentities.ModBlockEntities.MAP_BLOCK_ENTITY;

public class MapBlockEntity extends BlockEntity {
    private Vec3i regionSize;

    public MapBlockEntity(BlockPos pos, BlockState state) {
        super(MAP_BLOCK_ENTITY, pos, state);

        this.regionSize = new Vec3i(2, 2, 2);
    }

    public static void initialise() {}

    @Override
    protected void writeData(WriteView view) {
        if (this.regionSize != null) view.put("regionSize", Vec3i.CODEC, this.regionSize);

        super.writeData(view);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        this.regionSize = view.read("regionSize", Vec3i.CODEC).orElse(Vec3i.ZERO);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public Vec3i getRegionSize() {
        return this.regionSize;
    }
}
