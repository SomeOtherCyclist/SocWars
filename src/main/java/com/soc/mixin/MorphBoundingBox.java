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

import static com.soc.lib.SocWarsLib.floorXZ;

@Mixin(PlayerEntity.class)
abstract class MorphBoundingBox extends EntityGetBoundingBox {
	@Unique
	private static final Box FULL_BOX = new Box(0d, 0d, 0d, 1d, 1d, 1d);
	@Unique
	private static final Vec3d HALF_OFFSET = new Vec3d(-0.5d, 0d, -0.5d);

	@Override
	protected void socwars_getBoundingBox(CallbackInfoReturnable<Box> cir) {
		final PlayerData playerData = PlayerDataManager.getSideLocalPlayerData((PlayerEntity)(Object)this);
		if (playerData != null && playerData.getMorph() != null) {
			final VoxelShape collisionShape = playerData.getMorph().getOutlineShape(this.getWorld(), this.isSneaking() ? this.getBlockPos() : null);
			final Box boundingBox = collisionShape.isEmpty() ? FULL_BOX : collisionShape.getBoundingBox().offset((this.isSneaking() ? floorXZ(this.getPos()) : this.getPos().add(HALF_OFFSET)));
			cir.setReturnValue(boundingBox.stretch(0d, -collisionShape.getMin(Direction.Axis.Y), 0d));
		}
	}
}
