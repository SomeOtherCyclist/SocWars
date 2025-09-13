package com.soc;

import com.soc.blocks.util.ModBlocks;
import com.soc.entities.BigTntEntity;
import com.soc.networking.S2CReceivers;
import com.soc.renderer.BigTntRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.BlockRenderLayer;

@Environment(EnvType.CLIENT)
public class SocWarsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(BigTntEntity.NUCLEAR_BOMB, BigTntRenderer::new);
		EntityRendererRegistry.register(BigTntEntity.HYDROGEN_BOMB, BigTntRenderer::new);

		BlockRenderLayerMap.putBlock(ModBlocks.SPAWN_PLACEHOLDER, BlockRenderLayer.TRANSLUCENT);

		S2CReceivers.initialise();
	}
}
