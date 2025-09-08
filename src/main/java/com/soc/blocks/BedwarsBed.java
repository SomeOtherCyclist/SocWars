package com.soc.blocks;

import com.soc.SocWars;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BedwarsBed extends BedBlock {
    public BedwarsBed(DyeColor color, Settings settings) {
        super(color, settings);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        SocWars.LOGGER.error("This is onBreak()");
        SocWars.LOGGER.error("Maybe implement the actual block breaking event that would be smart");
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        SocWars.LOGGER.error("This is onStateReplaced()");
        SocWars.LOGGER.error("Maybe implement the actual block breaking event that would be smart");
        super.onStateReplaced(state, world, pos, moved);
    }
}
