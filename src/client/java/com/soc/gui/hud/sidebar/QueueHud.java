package com.soc.gui.hud.sidebar;

import com.soc.game.manager.GameType;
import com.soc.gui.hud.Reference;
import com.soc.gui.hud.SidebarHud;
import com.soc.gui.hud.VerticallyStackedHudComponent;
import com.soc.lib.SocWarsLib;
import com.soc.networking.helper.QueueProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import static com.soc.gui.hud.SidebarHud.SIDEBAR_WIDTH;
import static com.soc.gui.hud.SidebarHud.TIME_COLOURS;
import static com.soc.lib.SocWarsLib.enumerateMap;

public class QueueHud implements VerticallyStackedHudComponent {
	public static final @NotNull Reference<QueueHud> INSTANCE = new Reference<>();

	private static final int BLOCK_SIZE = 44;

	private Map<GameType, QueueProgress> progressMap;

	public static void initialise() {
		SidebarHud.addHudElement(INSTANCE);
	}

	public static void onJoinGame() {
		INSTANCE.annul();
	}

	@SuppressWarnings("DataFlowIssue")
	public static void receiveUpdate(Map<GameType, QueueProgress> progressMap) {
		if (progressMap.isEmpty()) {
			INSTANCE.annul();
			return;
		}

		if (INSTANCE.isNull()) {
			INSTANCE.set(new QueueHud(progressMap));
		} else {
			INSTANCE.get().progressMap = progressMap;
		}
	}

	public QueueHud(Map<GameType, QueueProgress> progressMap) {
		this.progressMap = progressMap;
	}

	@Override
	public int getSize() {
		return this.progressMap.size() * BLOCK_SIZE + 14;
	}

	@Override
	public int priority() {
		return 1;
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter renderTickCounter, TextRenderer textRenderer, int x, int y) {
		final MutableText title = Text.translatable("hud.queue.header");
		drawContext.drawText(textRenderer, title, x + (SIDEBAR_WIDTH - textRenderer.getWidth(title) >> 1), y + 4, 0xffffffff, true);

		final long time = Objects.requireNonNull(MinecraftClient.getInstance().world).getTime();
		enumerateMap(this.progressMap, (i, gameType, progress) -> {
			final int yStart = y + i * BLOCK_SIZE + 14;
			final MutableText timeText = SocWarsLib.getTimeFromSeconds(Math.max(0, (progress.completeTime() - time)) * 0.05f, false, TIME_COLOURS);

			drawContext.drawText(textRenderer, gameType.getVariantName(), x + 8, yStart + 4, 0xffffffff, true);

			final Formatting playerCountColour = progress.players() < gameType.minPlayers() ? Formatting.RED : progress.players() < gameType.maxPlayers() ? Formatting.GREEN : Formatting.DARK_GREEN;
			final MutableText playerCountText = Text.translatable("hud.queue.player_count", progress.players(), gameType.maxPlayers()).formatted(playerCountColour);
			drawContext.drawText(textRenderer, Text.translatable("hud.queue.players", playerCountText), x + 8, yStart + 18, 0xffffffff, true);

			final String startingTextKey = "hud.queue.progress." + (progress.players() > gameType.minPlayers() ? "starting" : progress.allowSinglePlayer() ? "force_starting" : "not_starting");
			drawContext.drawText(textRenderer, Text.translatable(startingTextKey, timeText), x + 8, yStart + 32, 0xffffffff, true);
		});
	}
}
