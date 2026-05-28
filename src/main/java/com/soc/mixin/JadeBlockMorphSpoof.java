package com.soc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.soc.player.Morph;
import com.soc.player.PlayerDataManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.api.Accessor;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.overlay.WailaTickHandler;

@Mixin(value = WailaTickHandler.class, remap = false)
abstract class JadeBlockMorphSpoof implements IMixinConfigPlugin {
	@Inject(method = "tickClient", at = @At(value = "INVOKE_ASSIGN", target = "Lsnownee/jade/api/EntityAccessor$Builder;build()Lsnownee/jade/api/EntityAccessor;"))
	private void socwars_jadeBlockMorphSpoof(CallbackInfo ci, @Local LocalRef<Accessor<?>> accessor, @Local LocalRef<HitResult> hitResult) {
		final EntityHitResult entityHitResult = (EntityHitResult)hitResult.get();
		if (!(entityHitResult.getEntity() instanceof PlayerEntity playerEntity)) return;

		final Morph morph = PlayerDataManager.getSideLocalPlayerData(playerEntity).getMorph();
		if (morph == null) return;

		final BlockHitResult blockTarget = new BlockHitResult(entityHitResult.getPos(), Direction.DOWN, entityHitResult.getEntity().getBlockPos(), false);
		hitResult.set(blockTarget);
		accessor.set(WailaClientRegistration.instance().blockAccessor().blockState(morph.blockState()).blockEntity((BlockEntity)null).hit(blockTarget).requireVerification().build());
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return FabricLoader.getInstance().isModLoaded("jade");
	}
}
