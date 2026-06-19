package com.soc.blocks;

import com.mojang.serialization.MapCodec;
import com.soc.blocks.blockentities.CollectibleBlockEntity;
import com.soc.blocks.blockentities.ModBlockEntities;
import com.soc.lib.ScoreboardHelper;
import com.soc.player.PlayerDataManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.block.SkullBlock.ROTATION;

public class CollectibleBlock extends BlockWithEntity {
    public CollectibleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(ROTATION, 0));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(CollectibleBlock::new);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CollectibleBlockEntity blockEntity)) {
            return super.onUse(state, world, pos, player, hit);
        }

        if (!world.isClient()) this.collect((ServerPlayerEntity)player, pos, blockEntity.getId());
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        final BlockState parent = super.getPlacementState(ctx);
        return parent == null ? null : parent.with(ROTATION, RotationPropertyHelper.fromYaw(ctx.getPlayerYaw()));
    }

    private void collect(ServerPlayerEntity player, BlockPos pos, int id) {
        boolean collected = !PlayerDataManager.getPlayerData(player).collectCollectible(id);

        player.sendMessage(Text.translatable(collected ? "collectible.collect" : "collectible.already_collected"), false); //Consolidate this garbage
        if (collected) {
            player.getWorld().markDirty(pos); //God I love sync stuff
            PlayerDataManager.sendData(player);

            ScoreboardHelper.collectDoubloons(player, 10);
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CollectibleBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient ? null : validateTicker(type, ModBlockEntities.COLLECTIBLE_BLOCK_ENTITY, CollectibleBlockEntity::clientTick);
    }
}