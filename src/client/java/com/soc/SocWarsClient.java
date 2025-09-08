package com.soc;

import com.soc.entities.BigTntEntity;
import com.soc.networking.S2CReceivers;
import com.soc.networking.s2c.PlayerDataPayload;
import com.soc.player.PlayerDataManager;
import com.soc.renderer.BigTntRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class SocWarsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(BigTntEntity.NUCLEAR_BOMB, BigTntRenderer::new);
		EntityRendererRegistry.register(BigTntEntity.HYDROGEN_BOMB, BigTntRenderer::new);

		S2CReceivers.initialise();
	}
}
