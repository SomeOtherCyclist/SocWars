package com.soc.mixin;

import com.soc.player.Morph;
import com.soc.player.PlayerData;
import com.soc.player.PlayerDataManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static java.lang.Math.floor;

@Mixin(PlayerEntity.class)
abstract class MorphBoundingBox extends EntityGetBoundingBox {
	@Override
	protected void socwars_getBoundingBox(CallbackInfoReturnable<Box> cir) {
		final PlayerData playerData = PlayerDataManager.getSideLocalPlayerData((PlayerEntity)(Object)this);
		if (playerData == null) return;

		final Morph morph = playerData.getMorph();
		if (morph == null) return;

		final Vec3d thisPos = this.getPos();
		final Box boundingBox = morph.boundingBox();

		cir.setReturnValue(morph.shouldSnap(this.isInSneakingPose(), this.getWorld(), this.getBlockPos()) ?
				boundingBox.offset(floor(thisPos.x), thisPos.y, floor(thisPos.z)) :
				boundingBox.offset(thisPos.x - 0.5d, thisPos.y, thisPos.z - 0.5d)
		);
	}
}
