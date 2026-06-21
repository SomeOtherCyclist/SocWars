package com.soc.renderer;

import com.soc.renderer.blockentity.CollectibleBlockEntityRenderer;
import com.soc.renderer.blockentity.DisplayBlockEntityRenderer;
import com.soc.renderer.blockentity.KitBlockEntityRenderer;
import com.soc.renderer.blockentity.MapBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

import static com.soc.blocks.blockentities.ModBlockEntities.*;

public interface BlockEntityRenderers {
	static void initialise() {
		BlockEntityRendererFactories.register(MAP_BLOCK_ENTITY, MapBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(COLLECTIBLE_BLOCK_ENTITY, CollectibleBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(DISPLAY_BLOCK_ENTITY, DisplayBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(KIT_BLOCK_ENTITY, KitBlockEntityRenderer::new);
	}
}
