package com.soc.blocks;

import com.mojang.serialization.MapCodec;
import com.soc.blocks.blockentities.CollectibleBlockEntity;
import com.soc.player.PlayerDataManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CollectibleBlock extends BlockWithEntity {
    public static final BooleanProperty HAS_COLLECTIBLE = BooleanProperty.of("has_collectible");

    public CollectibleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(HAS_COLLECTIBLE, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_COLLECTIBLE);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(CollectibleBlock::new);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CollectibleBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CollectibleBlockEntity blockEntity)) {
            return super.onUse(state, world, pos, player, hit);
        }

        if (!player.isCreative()) {
            this.collect(player, world, blockEntity);
            return ActionResult.SUCCESS;
        }

        final ItemStack stack = player.getStackInHand(Hand.MAIN_HAND); //Maybe clean up the spaghetti code below here at some point

        boolean validCollectible = !stack.isEmpty();
        if (!validCollectible && !player.isSneaking()) return ActionResult.SUCCESS;

        world.setBlockState(pos, state.with(HAS_COLLECTIBLE, validCollectible));
        blockEntity.setCollectible(validCollectible ? stack.getRegistryEntry() : null);

        return ActionResult.SUCCESS;
    }

    private void collect(PlayerEntity player, World world, CollectibleBlockEntity blockEntity) {
        if (blockEntity.getCollectible() == null) return;

        boolean collected = PlayerDataManager.getPlayerData(player).collectCollectible(blockEntity.getCollectible());
        if (world.isClient()) {
            player.sendMessage(Text.translatable(collected ? "collectible.collect" : "collectible.already_collected", blockEntity.getCollectible().value().getName()), false);
        } else if (collected) PlayerDataManager.collectDoubloons(player, 10);
    }
}