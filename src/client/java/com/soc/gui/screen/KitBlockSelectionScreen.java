package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.gui.widget.DisableableButtonWidget;
import com.soc.gui.widget.HoverTexturedButtonWidget;
import com.soc.gui.widget.ItemDisplayWidget;
import com.soc.gui.widget.ToggleButtonWidget;
import com.soc.lib.ScoreboardHelper;
import com.soc.networking.c2s.BuyKitPayload;
import com.soc.networking.c2s.KitSelectionPayload;
import com.soc.networking.helper.BlockLocation;
import com.soc.player.ClientPlayerDataManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.soc.lib.SocWarsLib.*;

public class KitBlockSelectionScreen extends Screen {
    public static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/kit_block_selection.png");
    public static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/button"), Identifier.ofVanilla("widget/button_disabled"), Identifier.ofVanilla("widget/button_highlighted"), Identifier.of(SocWars.MOD_ID, "widget/button_disabled_highlighted"));
    public static final ButtonTextures INSTANT_TEXTURES = new ButtonTextures(Identifier.of(SocWars.MOD_ID, "widget/leave_button"), Identifier.of(SocWars.MOD_ID, "widget/leave_button_highlighted"));
    public static final ButtonTextures REMOVE_TEXTURES = new ButtonTextures(Identifier.of(SocWars.MOD_ID, "widget/remove_button"), Identifier.of(SocWars.MOD_ID, "widget/remove_button_highlighted"));

    public static final Identifier COVER_TEXTURE = Identifier.of(SocWars.MOD_ID, "kit_block_selection_cover");

    public static final int HEIGHT = 162;
    public static final int WIDTH = 226;

    private final KitBlockEntity blockEntity;
    private boolean initialised;

    private final Map<GameType, Boolean> selectedGameTypes;

    private List<HoverTexturedButtonWidget> gameRemoveButtons;
    private List<ToggleButtonWidget> gameSelectionButtons;
    private List<HoverTexturedButtonWidget> gameInstantSelectionButtons;
    private DisableableButtonWidget selectKitButton;

    private DisableableButtonWidget buyKitButton;

    private List<ItemDisplayWidget> itemDisplays;

    public KitBlockSelectionScreen(KitBlockEntity blockEntity) {
        super(Text.translatable("screen.kit_block_selection"));
        this.blockEntity = blockEntity;
        this.selectedGameTypes = LinkedHashMap.newHashMap(blockEntity.getAllowedGameTypesList().size());

        for (GameType value : blockEntity.getAllowedGameTypesList()) {
            this.selectedGameTypes.put(value, true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        {
            final int i = (this.width - WIDTH) / 2;
            final int j = (this.height - HEIGHT) / 2;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0f, 0f, WIDTH, HEIGHT, WIDTH, HEIGHT);
        }

        super.render(context, mouseX, mouseY, deltaTicks);

        if (ClientPlayerDataManager.hasKitClient(this.blockEntity.getKit())) {
            enumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> {
                final boolean enabled = this.selectedGameTypes.get(gameType);
                final int y = this.height / 2 - 58 + i * 18;

                context.drawText(this.textRenderer, gameType.getCompactVariantName(), this.width / 2 - 83, y, enabled ? 0xff11ee22 : 0xffee1122, true);
                context.drawText(this.textRenderer, Text.translatable(enabled ? "hud.tick" : "hud.cross"), this.width / 2 - 19, y, enabled ? 0xff11ee22 : 0xffee1122, true);
            });
        } else {
            enumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> {
                final int y = this.height / 2 - 58 + i * 18;
                context.drawText(this.textRenderer, gameType.getCompactVariantName(), this.width / 2 - 83, y, 0xd0ffffff, true);
            });
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, COVER_TEXTURE, this.width / 2 - 106, this.height / 2 - 64, 118, 128);
        }

        this.drawTitle(context);
    }

    private void drawTitle(DrawContext context) {
        context.drawText(this.textRenderer, this.title, this.width / 2 - 105, this.height / 2 - 75, 0xff404040, false);

        final String kitName = this.blockEntity.getKit().getName();
        final int kitNameX = this.width / 2 + 105 - this.textRenderer.getWidth(kitName);
        context.drawText(this.textRenderer, kitName, kitNameX, this.height / 2 - 75, 0xff404040, false);
    }

    @Override
    protected void init() {
        if (!this.initialised) {
            this.createWidgets();
            this.initialised = true;
        }

        enumerate(this.gameRemoveButtons, (i, widget) -> {
            widget.setPosition(this.width / 2 - 104, this.height / 2 - 62 + i * 18);
            this.addDrawableChild(widget);
        });

        enumerate(this.gameSelectionButtons, (i, widget) -> {
            widget.setPosition(this.width / 2 - 86, this.height / 2 - 62 + i * 18);
            this.addDrawableChild(widget);
        });

        enumerate(this.gameInstantSelectionButtons, (i, widget) -> {
            widget.setPosition(this.width / 2 - 6, this.height / 2 - 62 + i * 18);
            this.addDrawableChild(widget);
        });

        this.selectKitButton.setPosition(this.width / 2 - 104, this.height / 2 + 46);
        this.addDrawableChild(this.selectKitButton);

        this.buyKitButton.setPosition(this.width / 2 - 104, this.height / 2 + 46);
        this.addDrawableChild(this.buyKitButton);

        enumerate(this.itemDisplays, (i, widget) -> {
            final int x = this.width / 2 + 17 + (i % 5) * 18;
            final int y = this.height / 2 + 29 + (i / 5) * 18;

            widget.setPosition(x, y);
            this.addDrawableChild(widget);
        });
    }

    private void createWidgets() {
        final boolean ownsKit = ClientPlayerDataManager.hasKitClient(this.blockEntity.getKit());

        this.gameRemoveButtons = mapEnumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> new HoverTexturedButtonWidget(0, 0, 16, 16, REMOVE_TEXTURES, button -> {
            ClientPlayNetworking.send(KitSelectionPayload.remove(new BlockLocation(this.blockEntity), List.of(gameType)));
            MinecraftClient.getInstance().setScreen(null);
        }, Text.translatable("button.kit_block.remove", gameType.getVariantName().formatted(Formatting.GOLD)))).toList();

        this.gameSelectionButtons = mapEnumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> new ToggleButtonWidget(0, 0, 78, 16, true, isToggled -> {
            this.selectedGameTypes.put(gameType, isToggled);
        }, TEXTURES, ownsKit)).toList();

        this.gameInstantSelectionButtons = mapEnumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> new HoverTexturedButtonWidget(0, 0, 16, 16, INSTANT_TEXTURES, button -> {
            ClientPlayNetworking.send(KitSelectionPayload.select(new BlockLocation(this.blockEntity), List.of(gameType)));
            MinecraftClient.getInstance().setScreen(null);
        }, Text.translatable("button.kit_block.instant_select", Text.literal(this.blockEntity.getKit().getName()).formatted(Formatting.GREEN), gameType.getVariantName().formatted(Formatting.GOLD)), ownsKit)).toList();

        this.selectKitButton = new DisableableButtonWidget(0, 0, 114, 16, Text.translatable("button.kit_block.select_kit"), widget -> {
            ClientPlayNetworking.send(KitSelectionPayload.select(new BlockLocation(this.blockEntity), this.selectedGameTypes.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList()));
            MinecraftClient.getInstance().setScreen(null);
        }, ownsKit);

        final Text displayName = mapIfNotNull(this.blockEntity.getWorld().getScoreboard().getNullableObjective(this.blockEntity.getScoreboardVariableName()), ScoreboardObjective::getDisplayName, Text.literal("MISSING"));
        this.buyKitButton = new DisableableButtonWidget(0, 0, 114, 16, Text.translatable(this.canAffordKit() ? "button.kit_block.buy_kit" : "button.kit_block.cannot_afford", this.blockEntity.getCost(), displayName), widget -> {
            this.buyKit();
        }, !ownsKit) {
            @Override
            public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
                super.drawMessage(context, textRenderer, KitBlockSelectionScreen.this.canAffordKit() ? 0xff20ff20 : 0xffff2020);
            }
        };
        this.buyKitButton.visible = !ownsKit;

        this.itemDisplays = mapEnumerate(this.blockEntity.getKit().getHeldStacks(), (i, stack) -> new ItemDisplayWidget(0, 0, 16, stack)).toList();
    }

    private void buyKit() {
		if (!this.canAffordKit()) return;

        this.gameSelectionButtons.forEach(widget -> widget.active = true);
        this.gameInstantSelectionButtons.forEach(widget -> widget.active = true);
        this.selectKitButton.active = true;

        this.buyKitButton.active = false;
        this.buyKitButton.visible = false;

        ClientPlayNetworking.send(new BuyKitPayload(this.blockEntity.getKit(), this.blockEntity.getCost(), this.blockEntity.getScoreboardVariableName()));
    }

    private boolean canAffordKit() {
        final boolean canAfford = ScoreboardHelper.scoreboardVariableIsGreater(Objects.requireNonNull(MinecraftClient.getInstance().player), this.blockEntity.getScoreboardVariableName(), this.blockEntity.getCost());
        final boolean ownsKit = ClientPlayerDataManager.hasKitClient(this.blockEntity.getKit());
        return canAfford && !ownsKit;
    }
}
