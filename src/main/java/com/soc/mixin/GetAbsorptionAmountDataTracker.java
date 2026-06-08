package com.soc.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntity.class)
public interface GetAbsorptionAmountDataTracker {
	@Accessor("ABSORPTION_AMOUNT")
	static TrackedData<Float> getAbsorption_Amount() {
		return null;
	}
}
