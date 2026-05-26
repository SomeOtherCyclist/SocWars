package com.soc.player;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public record Morph(BlockState blockState, Box boundingBox, float health) {
	public Morph(BlockState blockState, BlockView world) {
		this(blockState, makeBoundingBox(blockState, world), calculateMaxHealth(blockState, world));
	}

	private static Box makeBoundingBox(BlockState blockState, BlockView world) {
		final VoxelShape baseShape = blockState.getCollisionShape(world, null);
		return baseShape.getBoundingBox().stretch(0d, -baseShape.getMin(Direction.Axis.Y), 0d);
	}

	private static float calculateMaxHealth(BlockState blockState, BlockView world) {
		final VoxelShape baseShape = blockState.getCollisionShape(world, null);
		final Box baseBox = baseShape.getBoundingBox();
		final double boxVolume = baseBox.getLengthX() * baseBox.getLengthY() * baseBox.getLengthZ();
		return (float)Math.clamp(Math.pow(boxVolume, 0.25f), 2f, 40f);
	}
}
