package com.soc;

import com.soc.blocks.util.ModBlocks;
import com.soc.gui.hud.*;
import com.soc.gui.hud.sidebar.BedwarsTeamsHud;
import com.soc.gui.hud.sidebar.EventsHud;
import com.soc.gui.hud.sidebar.QueueHud;
import com.soc.gui.hud.sidebar.SkywarsTeamsHud;
import com.soc.gui.screen.HandledScreens;
import com.soc.gui.screen.QueueScreen;
import com.soc.items.FeatherBlockItem;
import com.soc.lib.Coroutines;
import com.soc.lib.Events;
import com.soc.model.EntityModelLayers;
import com.soc.networking.S2CReceivers;
import com.soc.player.ClientPlayerDataManager;
import com.soc.player.Hotkeys;
import com.soc.renderer.*;
import com.soc.resourcedata.deserialisation.SkywarsItemData;
import com.soc.resourcedata.listeners.SkywarsLootData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Map;

import static com.soc.blocks.blockentities.ModBlockEntities.*;

@Environment(EnvType.CLIENT)
public class SocWarsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(MAP_BLOCK_ENTITY, MapBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(COLLECTIBLE_BLOCK_ENTITY, CollectibleBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(DISPLAY_BLOCK_ENTITY, DisplayBlockEntityRenderer::new);

		BlockRenderLayerMap.putBlock(ModBlocks.SPAWN_PLACEHOLDER, BlockRenderLayer.TRANSLUCENT);
		BlockRenderLayerMap.putBlock(ModBlocks.ITSEVOCAT_SKULL, BlockRenderLayer.TRANSLUCENT);

		S2CReceivers.initialise();
		HandledScreens.initialise();
		ClientPlayerDataManager.initialise();
		Hotkeys.initialise();

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (!client.isIntegratedServerRunning()) {
				Coroutines.getInstance().runCoroutines();
				Events.getInstance().tryRunEvents();
			}
		});

		WorldRenderEvents.BEFORE_ENTITIES.register(renderContext -> {
			final MinecraftClient client = MinecraftClient.getInstance();

			final boolean mainHandCheck = client.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof FeatherBlockItem;
			final boolean offHandCheck = client.player.getStackInHand(Hand.OFF_HAND).getItem() instanceof FeatherBlockItem;
			if (!mainHandCheck && !offHandCheck) return;

			final float tickProgress = renderContext.tickCounter().getTickProgress(true);
			final Vec3d lookOffset = client.player.getRotationVec(tickProgress).multiply(client.player.getBlockInteractionRange() - 1d);

			final BlockPos pos = BlockPos.ofFloored(client.player.getEyePos().add(lookOffset));
			if (client.crosshairTarget.getType() == HitResult.Type.BLOCK) return;

			final WorldRenderContext context = WorldRenderContext.getInstance(client.worldRenderer);
			final MatrixStack matrices = context.matrixStack();
			final VertexConsumer consumer = context.consumers().getBuffer(RenderLayer.LINES);

			final int colour = Color.HSBtoRGB(client.world.getTime() / 50f, 1, 1);

			matrices.push();
			matrices.translate(context.camera().getCameraPos().multiply(-1d));
			matrices.translate(Vec3d.of(pos));
			VertexRendering.drawOutline(
					matrices,
					consumer,
					VoxelShapes.fullCube(),
					0d,
					0d,
					0d,
					colour
			);
			matrices.pop();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			try {
				final WorldRenderContext context = WorldRenderContext.getInstance(client.worldRenderer);
				final MatrixStack matrices = context.matrixStack();
				final VertexConsumer consumer = context.consumers().getBuffer(RenderLayer.LINES);

				final BlockPos pos = BlockPos.ofFloored(client.player.getPos());

				matrices.push();
				matrices.translate(pos.toCenterPos());
				VertexRendering.drawOutline(
						matrices,
						consumer,
						VoxelShapes.fullCube(),
						0,
						0,
						0,
						0
				);
				matrices.pop();
			} catch (Exception ignored) {}
		});

		SidebarHud.initialise();
		BedwarsTeamsHud.initialise();
		SkywarsTeamsHud.initialise();
		EventsHud.initialise();
		QueueHud.initialise();
		BlockProtectionManagerAndHud.initialise();
		EntityModelLayers.initialise();
		EntityRenderers.initialise();
	}
}
