package com.soc.mixin.client;

import com.soc.blocks.MapBlock;
import com.soc.blocks.blockentities.MapBlockEntity;
import com.soc.screens.MapBlockScreen;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapBlock.class)
public class OpenMapBlockScreen {
	@Inject(at = @At("HEAD"), method = "onUse")
	private void socwars_openMapBlockScreen(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (world.isClient() && world.getBlockEntity(pos) instanceof MapBlockEntity mapBlockEntity) {
			final MapBlockScreen screen = new MapBlockScreen(mapBlockEntity);
			MinecraftClient.getInstance().setScreen(screen);
		}
	}
}