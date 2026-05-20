package com.soc.mixin;

import com.soc.player.PlayerData;
import com.soc.player.PlayerDataManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static java.lang.Math.floor;

@Mixin(PlayerEntity.class)
abstract class MorphBoundingBox extends EntityGetBoundingBox {
	@Unique
	private static final Box FULL_BOX = new Box(0d, 0d, 0d, 1d, 1d, 1d);

	@Override
	protected void socwars_getBoundingBox(CallbackInfoReturnable<Box> cir) {
		final PlayerData playerData = PlayerDataManager.getSideLocalPlayerData((PlayerEntity)(Object)this);
		if (playerData != null && playerData.getMorph() != null) {
			final Vec3d thisPos = this.getPos();

			final Box boundingBox;
			final VoxelShape collisionShape;

			if (this.isSneaking()) {
				collisionShape = playerData.getMorph().getOutlineShape(this.getWorld(), this.getBlockPos());
				boundingBox = collisionShape.isEmpty() ? FULL_BOX : collisionShape.getBoundingBox().offset(floor(thisPos.x), floor(thisPos.y), floor(thisPos.z));

			} else {
				collisionShape = playerData.getMorph().getOutlineShape(this.getWorld(), null);
				boundingBox = collisionShape.isEmpty() ? FULL_BOX : collisionShape.getBoundingBox().offset(thisPos.x - 0.5d, thisPos.y, thisPos.z - 0.5d);
			}
			cir.setReturnValue(boundingBox.stretch(0d, -collisionShape.getMin(Direction.Axis.Y), 0d));
		}
	}
}
