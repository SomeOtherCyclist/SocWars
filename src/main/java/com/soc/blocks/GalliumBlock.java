package com.soc.blocks;

import com.soc.util.DamageTypes;
import com.soc.util.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ColoredFallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ColorCode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.soc.lib.SocWarsLib.damageSource;

public class GalliumBlock extends ColoredFallingBlock {
    public GalliumBlock(Settings settings) {
        super(new ColorCode(0), settings);
    }

    public GalliumBlock(ColorCode color, Settings settings) {
        super(color, settings);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        final BlockPos posDown = pos.down();
        if (!world.getBlockState(posDown).isIn(ModBlockTags.IMMUNE)) {
            world.setBlockState(posDown, Blocks.AIR.getDefaultState());
        }

        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
        if (entity instanceof final LivingEntity livingEntity && !world.isClient()) {
            final float damage = livingEntity.getArmor() * 0.045f + 0.9f;
            final DamageSource source = damageSource(world, DamageTypes.GALLIUM);
            livingEntity.damage((ServerWorld)world, source, damage);
        }
    }
}
