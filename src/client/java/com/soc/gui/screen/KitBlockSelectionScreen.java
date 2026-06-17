package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.gui.widget.DisableableButtonWidget;
import com.soc.gui.widget.HoverTexturedButtonWidget;
import com.soc.gui.widget.ItemDisplayWidget;
import com.soc.gui.widget.ToggleButtonWidget;
import com.soc.networking.c2s.BuyKitPayload;
import com.soc.networking.c2s.KitSelectionPayload;
import com.soc.networking.helper.BlockLocation;
import com.soc.player.ClientPlayerDataManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.soc.lib.SocWarsLib.*;

public class KitBlockSelectionScreen extends Screen {
    public static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/kit_block_selection.png");
    public static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/button"), Identifier.ofVanilla("widget/button_disabled"), Identifier.ofVanilla("widget/button_highlighted"), Identifier.of(SocWars.MOD_ID, "widget/button_disabled_highlighted"));
    public static final ButtonTextures INSTANT_TEXTURES = new ButtonTextures(Identifier.of(SocWars.MOD_ID, "widget/leave_button"), Identifier.of(SocWars.MOD_ID, "widget/leave_button_highlighted"));

    public static final Identifier COVER_TEXTURE = Identifier.of(SocWars.MOD_ID, "kit_block_selection_cover");

    public static final int HEIGHT = 162;
    public static final int WIDTH = 208;

    private final KitBlockEntity blockEntity;
    private boolean initialised;

    private final Map<GameType, Boolean> selectedGameTypes;

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

                context.drawText(this.textRenderer, gameType.getCompactVariantName(), this.width / 2 - 92, y, enabled ? 0xff11ee22 : 0xffee1122, true);
                context.drawText(this.textRenderer, Text.translatable(enabled ? "hud.tick" : "hud.cross"), this.width / 2 - 28, y, enabled ? 0xff11ee22 : 0xffee1122, true);
            });
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, COVER_TEXTURE, this.width / 2 - 97, this.height / 2 - 64, 100, 128);
        }

        this.drawTitle(context);
    }

    private void drawTitle(DrawContext context) {
        context.drawText(this.textRenderer, this.title, this.width / 2 - 96, this.height / 2 - 75, 0xff404040, false);

        final String kitName = this.blockEntity.getKit().getName();
        final int kitNameX = this.width / 2 + 96 - this.textRenderer.getWidth(kitName);
        context.drawText(this.textRenderer, kitName, kitNameX, this.height / 2 - 75, 0xff404040, false);
    }

    @Override
    protected void init() {
        if (!this.initialised) {
            this.createWidgets();
            this.initialised = true;
        }

        enumerate(this.gameSelectionButtons, (i, widget) -> {
            widget.setPosition(this.width / 2 - 95, this.height / 2 - 62 + i * 18);
            this.addDrawableChild(widget);
        });
        enumerate(this.gameInstantSelectionButtons, (i, widget) -> {
            widget.setPosition(this.width / 2 - 15, this.height / 2 - 62 + i * 18);
            this.addDrawableChild(widget);
        });

        this.selectKitButton.setPosition(this.width / 2 - 95, this.height / 2 + 46);
        this.addDrawableChild(this.selectKitButton);

        this.buyKitButton.setPosition(this.width / 2 - 95, this.height / 2 - 4);
        this.addDrawableChild(this.buyKitButton);

        enumerate(this.itemDisplays, (i, widget) -> {
            final int x = this.width / 2 + 8 + (i % 5) * 18;
            final int y = this.height / 2 + 29 + (i / 5) * 18;

            widget.setPosition(x, y);
            this.addDrawableChild(widget);
        });
    }

    private void createWidgets() {
        final boolean ownsKit = ClientPlayerDataManager.hasKitClient(this.blockEntity.getKit());

        this.gameSelectionButtons = mapEnumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> new ToggleButtonWidget(0, 0, 78, 16, true, isToggled -> {
            this.selectedGameTypes.put(gameType, isToggled);
        }, TEXTURES, ownsKit)).toList();
        this.gameInstantSelectionButtons = mapEnumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> new HoverTexturedButtonWidget(0, 0, 16, 16, INSTANT_TEXTURES, button -> {
            ClientPlayNetworking.send(new KitSelectionPayload(new BlockLocation(this.blockEntity), List.of(gameType)));
            MinecraftClient.getInstance().setScreen(null);
        }, Text.translatable("button.kit_block.instant_select", gameType.getVariantName().formatted(Formatting.GOLD)), ownsKit)).toList();

        this.selectKitButton = new DisableableButtonWidget(0, 0, 96, 16, Text.translatable("text.kit_block.select_kit"), widget -> {
            ClientPlayNetworking.send(new KitSelectionPayload(new BlockLocation(this.blockEntity), this.selectedGameTypes.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList()));
            MinecraftClient.getInstance().setScreen(null);
        }, ownsKit);

        final Text displayName = mapIfNotNull(this.blockEntity.getWorld().getScoreboard().getNullableObjective(this.blockEntity.getScoreboardVariableName()), ScoreboardObjective::getDisplayName, Text.literal("MISSING"));
        this.buyKitButton = new DisableableButtonWidget(0, 0, 96, 16, Text.translatable("text.kit_block.buy_kit", this.blockEntity.getCost(), displayName), widget -> {
            this.buyKit(); //Do checks
        }, !ownsKit);
        this.buyKitButton.visible = !ownsKit;

        this.itemDisplays = mapEnumerate(this.blockEntity.getKit().getHeldStacks(), (i, stack) -> new ItemDisplayWidget(0, 0, 16, stack)).toList();
    }

    private void buyKit() {
        this.gameSelectionButtons.forEach(widget -> widget.active = true);
        this.gameInstantSelectionButtons.forEach(widget -> widget.active = true);
        this.selectKitButton.active = true;

        this.buyKitButton.active = false;
        this.buyKitButton.visible = false;

        ClientPlayNetworking.send(new BuyKitPayload(this.blockEntity.getKit(), this.blockEntity.getCost(), this.blockEntity.getScoreboardVariableName()));
    }
}
