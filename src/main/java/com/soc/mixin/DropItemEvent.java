package com.soc.mixin;

import com.soc.events.ModEvents;
import com.soc.items.GamblerSword;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class DropItemEvent {
	@Inject(at = @At("HEAD"), method = "dropItem")
	private void socwars_dropItemEvent(ItemStack stack, boolean dropAtSelf, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
		if ((Object)this instanceof ServerPlayerEntity serverPlayer) {
			if (!ModEvents.ON_ITEM_DROPPED.invoker().onDropItem(serverPlayer, stack)) cir.cancel();
		}

		//Hot garbage code, maybe fix this sometime
		final EntityAttributeInstance attributeInstance = ((LivingEntity)(Object)this).getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
		if (attributeInstance != null) {
			attributeInstance.removeModifier(GamblerSword.ATTRIBUTE_ID);
		}
	}
}