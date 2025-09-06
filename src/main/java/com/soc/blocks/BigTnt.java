package com.soc.blocks;

import com.soc.blocks.util.ModBlocks;
import com.soc.entities.BigTntEntity;
import com.soc.util.Sounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class BigTnt extends Block {
    private final BigTntEntity.BigTntType tntType;

    public BigTnt(Settings settings, BigTntEntity.BigTntType tntType) {
        super(settings);
        this.tntType = tntType;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }

        this.ignite(world, pos, player);

        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isReceivingRedstonePower(pos)) this.ignite(world, pos, null);
    }

    private void ignite(World world, BlockPos pos, PlayerEntity player) {
        BigTntEntity entity = new BigTntEntity(world, pos.toBottomCenterPos(), player, tntType);
        world.spawnEntity(entity);
        world.playSound(null, pos, Sounds.NUCLEAR_SIREN, SoundCategory.BLOCKS,1, 1);
        world.removeBlock(pos, false);
    }
}
