package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.game.manager.GameType;
import com.soc.networking.helper.QueueProgress;
import com.soc.networking.s2c.QueuePayload;
import com.soc.player.Hotkeys;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.soc.lib.SocWarsLib.*;

public class QueueScreen extends Screen {
	private static final Map<GameType, Identifier> BACKGROUND_TEXTURES = Map.of(
			GameType.SKYWARS, Identifier.of(SocWars.MOD_ID, "widget/game_icons/skywars"),
			GameType.BEDWARS, Identifier.of(SocWars.MOD_ID, "widget/game_icons/bedwars"),
			GameType.PROP_HUNT, Identifier.of(SocWars.MOD_ID, "widget/game_icons/prop_hunt"),
			GameType.HIDE_AND_SEEK, Identifier.of(SocWars.MOD_ID, "widget/game_icons/hide_and_seek")
	);

	private boolean initialised = false;

	private final Map<GameType, Boolean> selectedGameTypes;
	private List<ButtonWidget> queueButtons;
	private final boolean allowsMultiQueue;

	private Map<GameType, QueueProgress> queueProgress;

	public QueueScreen(Collection<GameType> selectedGameTypes, boolean allowsMultiQueue) {
		super(Text.translatable(""));
		this.allowsMultiQueue = allowsMultiQueue;

		this.selectedGameTypes = HashMap.newHashMap(GameType.values().length);
		for (GameType selectedGameType : selectedGameTypes) {
			this.selectedGameTypes.put(selectedGameType, true);
		}
	}

	private void createWidgets() {
		this.queueButtons = mapEnumerate(GameType.values(), (i, gameType) -> ButtonWidget.builder(Text.empty(), widget -> {
			boolean currentValue = this.selectedGameTypes.getOrDefault(gameType, false);

			if (!this.allowsMultiQueue) this.selectedGameTypes.clear();

			this.selectedGameTypes.put(gameType, !currentValue);
			this.syncSelectedQueuesToServer();
		}).build()).toList();
	}

	@Override
	protected void init() {
		if (!this.initialised) {
			this.createWidgets();
			this.initialised = true;
		}

		enumerate(this.queueButtons, (i, widget) -> {
			widget.setPosition(this.width / 2 + 192 * (i - 2) + 8, this.height / 2 - 140);
			widget.setDimensions(176, 300);
			this.addDrawableChild(widget);
		});
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);

		final Matrix3x2fStack matrices = context.getMatrices();

		this.drawBackgroundImages(context);
		this.drawLabels(context, matrices);
	}

	private void drawBackgroundImages(DrawContext context) {
		enumerate(GameType.values(), (i, gameType) -> {
			final int xStart = this.width / 2 + 192 * (i - 2) + 8;
			final int yStart = this.height / 2 - 140;

			ifNotNull(BACKGROUND_TEXTURES.get(gameType), texture -> context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, xStart, yStart, 176, 300));
		});
	}

	private void drawLabels(DrawContext context, Matrix3x2fStack matrices) {
		enumerate(GameType.values(), (i, gameType) -> {
			//Draw dark band first
			final int xStart = this.width / 2 + 192 * (i - 2) + 8;
			final int yStart = this.height / 2 + 70;
			context.fill(xStart, yStart, xStart + 176, yStart + 36, 0x80000000);

			//Then actually draw the text afterwards idiot
			final MutableText variantName = gameType.getVariantName();
			final boolean enabled = this.selectedGameTypes.getOrDefault(gameType, false);

			matrices.pushMatrix();
			matrices.scaleAround(2.2f, (float)this.width / 2 + 192 * (i - 2) + 20, (float)this.height / 2 + 80);

			context.drawText(this.textRenderer, variantName, this.width / 2 + 192 * (i - 2) + 20, this.height / 2 + 80, enabled ? 0xff11ee22 : 0xffee1122, true);

			matrices.popMatrix();
		});
	}

	public void setQueueProgress(Map<GameType, QueueProgress> queueProgress) {
		this.queueProgress = queueProgress;
	}

	private void syncSelectedQueuesToServer() {
		ClientPlayNetworking.send(new QueuePayload(this.selectedGameTypes.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList()));
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (Hotkeys.OPEN_QUEUE_SCREEN.matchesKey(keyCode, scanCode)) {
			this.close();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
