package com.soc.mixin.client;

import com.soc.blocks.KitBlock;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.gui.screen.KitBlockSelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = KitBlock.class, remap = false)
public abstract class OpenKitBlockSelectionScreen {
	@Inject(at = @At("HEAD"), method = "openKitSelectionScreen")
	private void socwars_openKitBlockSelectionScreen(KitBlockEntity blockEntity, CallbackInfo ci) {
		if (Objects.requireNonNull(blockEntity.getWorld()).isClient) {
			if (blockEntity.hasValidKit()) {
				final KitBlockSelectionScreen screen = new KitBlockSelectionScreen(blockEntity);
				MinecraftClient.getInstance().setScreen(screen);
			} else {
				Objects.requireNonNull(MinecraftClient.getInstance().player).sendMessage(Text.translatable("message.invalid_kit"), false);
			}
		}
	}
}