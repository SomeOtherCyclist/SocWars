package com.soc.mixin;

import com.soc.player.PlayerDataManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import snownee.jade.overlay.RayTracing;
import snownee.jade.overlay.WailaTickHandler;

@Mixin(value = WailaTickHandler.class, remap = false)
abstract class JadeBlockMorphSpoof implements IMixinConfigPlugin {
	@Redirect(method = "tickClient", at = @At(value = "INVOKE", target = "Lsnownee/jade/overlay/RayTracing;getTarget()Lnet/minecraft/util/hit/HitResult;"))
	private HitResult socwars_jadeBlockMorphSpoof(RayTracing instance) {
		final HitResult target = instance.getTarget();
		if (!(target instanceof EntityHitResult entityHitResult) || !(entityHitResult.getEntity() instanceof PlayerEntity playerEntity)) return target;

		final BlockState morph = PlayerDataManager.getSideLocalPlayerData(playerEntity).getMorph();
		if (morph == null) return target;

		return new BlockHitResult(target.getPos(), Direction.DOWN, entityHitResult.getEntity().getBlockPos(), false);
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return FabricLoader.getInstance().isModLoaded("jade");
	}
}
