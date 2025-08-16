package com.soc.mixin;

import com.soc.items.GamblerSword;
import com.soc.players.PlayerData;
import com.soc.players.PlayerDataManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class DropItemEvent {
	@Inject(at = @At("HEAD"), method = "dropItem")
	private void socwars_dropItemEvent(ItemStack stack, boolean dropAtSelf, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
		LivingEntity self = ((LivingEntity)(Object)this);
		EntityAttributeInstance attributeInstance = self.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
		if (attributeInstance != null) {
			attributeInstance.removeModifier(GamblerSword.ATTRIBUTE_ID);
			//Hot garbage code, maybe fix this sometime
		}
	}
}