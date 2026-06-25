package com.soc.mixin;

import com.soc.items.components.ModComponents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class DoubleJump {
	@Shadow private int jumpingCooldown;

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isOnGround()Z", ordinal = 2), method = "tickMovement")
	protected boolean socwars_doubleJump(LivingEntity instance) {
 		if (instance.getType() == EntityType.PLAYER && !instance.isOnGround() && this.jumpingCooldown == 0) {
			final PlayerEntity player = (PlayerEntity)instance;

			boolean canDoubleJump = false;

			for (Hand hand : Hand.values()) {
				final ItemStack stack = player.getStackInHand(hand);
				final Boolean stackIsActive = stack.get(ModComponents.DOUBLE_JUMP);
				final boolean unBoxed = stackIsActive != null && stackIsActive;
				if (unBoxed) {
					stack.set(ModComponents.DOUBLE_JUMP, false);
					player.swingHand(hand);
					canDoubleJump = true;
				}
			}

			return canDoubleJump;
		} else {
			return instance.isOnGround();
		}
	}
}