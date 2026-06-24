package com.soc.mixin;

import com.soc.player.PlayerData;
import com.soc.player.PlayerDataManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.DoorBlock.OPEN;

@Mixin(DoorBlock.class)
abstract class NoMorphedDoorCollisions extends AbstractBlockMixin {
	@Override
	protected void socwars_getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
		if (state.get(OPEN) && context instanceof EntityShapeContext entityContext && entityContext.getEntity() != null) {
			final PlayerData playerData = PlayerDataManager.getSideLocalPlayerDataEntity(entityContext.getEntity());
			if (playerData != null && playerData.getMorph() != null) cir.setReturnValue(VoxelShapes.empty());
		}
	}
}
