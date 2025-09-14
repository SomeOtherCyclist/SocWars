package com.soc.mixin.client;

import com.soc.blocks.MapBlock;
import com.soc.blocks.blockentities.MapBlockEntity;
import com.soc.gui.screen.MapBlockScreen;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapBlock.class)
public class OpenMapBlockScreen {
	@Inject(at = @At("HEAD"), method = "onUse")
	private void socwars_openMapBlockScreen(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (world.isClient() && world.getBlockEntity(pos) instanceof MapBlockEntity mapBlockEntity) {
			final MapBlockScreen swScreen = new MapBlockScreen(mapBlockEntity, world);
			MinecraftClient.getInstance().setScreen(swScreen);
		}
	}
}