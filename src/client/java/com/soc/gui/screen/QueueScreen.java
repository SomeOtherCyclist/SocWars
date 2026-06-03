package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.game.manager.GameType;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.soc.lib.SocWarsLib.*;

public class QueueScreen extends Screen {
	private static final boolean SINGLE_QUEUE = true;
	private static final Map<GameType, Identifier> BACKGROUND_TEXTURES = Map.of(
			GameType.SKYWARS, Identifier.of(SocWars.MOD_ID, "widget/game_icons/skywars"),
			GameType.BEDWARS, Identifier.of(SocWars.MOD_ID, "widget/game_icons/bedwars"),
			GameType.PROP_HUNT, Identifier.of(SocWars.MOD_ID, "widget/game_icons/prop_hunt"),
			GameType.HIDE_AND_SEEK, Identifier.of(SocWars.MOD_ID, "widget/game_icons/hide_and_seek")
	);

	private boolean initialised = false;

	private final Map<GameType, Boolean> selectedGameTypes;

	private List<ButtonWidget> queueButtons;

	public QueueScreen(Text title) {
		super(title);

		this.selectedGameTypes = HashMap.newHashMap(GameType.values().length);
	}

	private void createWidgets() {
		this.queueButtons = mapEnumerate(GameType.values(), (i, gameType) -> ButtonWidget.builder(Text.empty(), widget -> {
			boolean currentValue = this.selectedGameTypes.getOrDefault(gameType, false);

			if (SINGLE_QUEUE) this.selectedGameTypes.clear();

			this.selectedGameTypes.put(gameType, !currentValue);
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

		enumerate(GameType.values(), (i, gameType) -> {
			final int xStart = this.width / 2 + 192 * (i - 2) + 8;
			final int yStart = this.height / 2 - 140;

			ifNotNull(BACKGROUND_TEXTURES.get(gameType), texture -> context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, xStart, yStart, 176, 300));
		});

		enumerate(GameType.values(), (i, gameType) -> {
			final int xStart = this.width / 2 + 192 * (i - 2) + 8;
			final int yStart = this.height / 2 + 70;
			context.fill(xStart, yStart, xStart + 176, yStart + 36, 0x80000000);
		});

		this.drawText(context, matrices);
	}

	private void drawText(DrawContext context, Matrix3x2fStack matrices) {
		enumerate(GameType.values(), (i, gameType) -> {
			final MutableText variantName = gameType.getVariantName();
			final boolean enabled = this.selectedGameTypes.getOrDefault(gameType, false);

			matrices.pushMatrix();
			matrices.scaleAround(2.2f, (float)this.width / 2 + 192 * (i - 2) + 20, (float)this.height / 2 + 80);

			context.drawText(this.textRenderer, variantName, this.width / 2 + 192 * (i - 2) + 20, this.height / 2 + 80, enabled ? 0xff11ee22 : 0xffee1122, true);

			matrices.popMatrix();
		});
	}
}
