package com.soc;

import com.soc.blocks.util.ModBlocks;
import com.soc.entities.BigTntEntity;
import com.soc.networking.S2CReceivers;
import com.soc.renderer.BigTntRenderer;
import com.soc.renderer.CollectibleBlockEntityRenderer;
import com.soc.renderer.MapBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import static com.soc.blocks.blockentities.ModBlockEntities.COLLECTIBLE_BLOCK_ENTITY;
import static com.soc.blocks.blockentities.ModBlockEntities.MAP_BLOCK_ENTITY;

@Environment(EnvType.CLIENT)
public class SocWarsClient implements ClientModInitializer {
	private static KeyBinding KEY_BINDING;

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(BigTntEntity.NUCLEAR_BOMB, BigTntRenderer::new);
		EntityRendererRegistry.register(BigTntEntity.HYDROGEN_BOMB, BigTntRenderer::new);

		BlockEntityRendererFactories.register(MAP_BLOCK_ENTITY, MapBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(COLLECTIBLE_BLOCK_ENTITY, CollectibleBlockEntityRenderer::new);

		BlockRenderLayerMap.putBlock(ModBlocks.SPAWN_PLACEHOLDER, BlockRenderLayer.TRANSLUCENT);

		S2CReceivers.initialise();

		KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.socwars.print_held_components",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_H,
				"category.socwars.debug"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (KEY_BINDING.wasPressed()) {
				final ClientPlayerEntity player = client.player;
				player.getStackInHand(Hand.MAIN_HAND).getComponents().forEach(component -> player.sendMessage(Text.literal(component.toString()), false));
			}
		});
	}
}
