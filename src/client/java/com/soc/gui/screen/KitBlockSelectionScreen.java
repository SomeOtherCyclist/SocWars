package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.gui.widget.HoverTexturedButtonWidget;
import com.soc.gui.widget.ItemDisplayWidget;
import com.soc.gui.widget.ToggleButtonWidget;
import com.soc.networking.c2s.KitSelectionPayload;
import com.soc.networking.helper.BlockLocation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.soc.lib.SocWarsLib.enumerate;
import static com.soc.lib.SocWarsLib.mapEnumerate;

public class KitBlockSelectionScreen extends Screen {
    public static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/kit_block_selection.png");
    public static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/button"), Identifier.ofVanilla("widget/button_disabled"), Identifier.ofVanilla("widget/button_highlighted"), Identifier.of(SocWars.MOD_ID, "widget/button_disabled_highlighted"));
    public static final ButtonTextures INSTANT_TEXTURES = new ButtonTextures(Identifier.of(SocWars.MOD_ID, "widget/leave_button"), Identifier.of(SocWars.MOD_ID, "widget/leave_button_highlighted"));

    public static final int ITEMS_START_X = 77;
    public static final int ITEMS_START_Y = -53;

    private final KitBlockEntity blockEntity;
    private boolean initialised;

    private final Map<GameType, Boolean> selectedGameTypes;

    private List<ToggleButtonWidget> gameSelectionButtons;
    private List<HoverTexturedButtonWidget> gameInstantSelectionButtons;
    private ButtonWidget selectKitButton;

    private List<ItemDisplayWidget> itemDisplays;


    public KitBlockSelectionScreen(KitBlockEntity blockEntity) {
        super(Text.translatable("screen.kit_block_selection"));
        this.blockEntity = blockEntity;
        this.selectedGameTypes = HashMap.newHashMap(GameType.values().length);

        for (GameType value : blockEntity.getAllowedGameTypesList()) {
            this.selectedGameTypes.put(value, true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        {
            final int i = (this.width - 238) / 2;
            final int j = (this.height - 142) / 2;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, 238, 142, 238, 142);
        }

        super.render(context, mouseX, mouseY, deltaTicks);

        enumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> {
            final boolean enabled = this.selectedGameTypes.get(gameType);
            final int y = this.height / 2 - 48 + i * 18;

            context.drawText(this.textRenderer, gameType.getCompactVariantName(), this.width / 2 - 107, y, enabled ? 0xff11ee22 : 0xffee1122, true);
            context.drawText(this.textRenderer, Text.translatable(enabled ? "hud.tick" : "hud.cross"), this.width / 2 - 42, y, enabled ? 0xff11ee22 : 0xffee1122, true);
        });

        this.drawTitle(context);
    }

    private void drawTitle(DrawContext context) {
        context.drawText(this.textRenderer, this.title, this.width / 2 - 111, this.height / 2 - 65, 0xff404040, false);

        final String kitName = this.blockEntity.getKit().getName();
        final int kitNameX = this.width / 2 + 111 - this.textRenderer.getWidth(kitName);
        context.drawText(this.textRenderer, kitName, kitNameX, this.height / 2 - 65, 0xff404040, false);
    }

    @Override
    protected void init() {
        if (!this.initialised) {
            this.createWidgets();
            this.initialised = true;
        }

        enumerate(this.gameSelectionButtons, (i, widget) -> {
            widget.setPosition(this.width / 2 - 110, this.height / 2 - 52 + i * 18);
            this.addDrawableChild(widget);
        });
        enumerate(this.gameInstantSelectionButtons, (i, widget) -> {
            widget.setPosition(this.width / 2 - 30, this.height / 2 - 52 + i * 18);
            this.addDrawableChild(widget);
        });

        this.selectKitButton.setPosition(this.width / 2 - 110, this.height / 2 + 36);
        this.addDrawableChild(this.selectKitButton);

        enumerate(this.itemDisplays, (i, widget) -> {
            final int x = this.width / 2 + ITEMS_START_X + (i % 2) * 18;
            final int y = this.height / 2 + ITEMS_START_Y + (i >> 1) * 18;

            widget.setPosition(x, y);
            this.addDrawableChild(widget);
        });
    }

    private void createWidgets() {
        this.gameSelectionButtons = mapEnumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> new ToggleButtonWidget(this.width / 2 - 110, this.height / 2 - 52 + i * 18, 78, 16, true, isToggled -> {
            this.selectedGameTypes.put(gameType, isToggled);
        }, TEXTURES)).toList();
        this.gameInstantSelectionButtons = mapEnumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> new HoverTexturedButtonWidget(this.width / 2 - 30, this.height / 2 - 52 + i * 18, 16, 16, INSTANT_TEXTURES, button -> {
            ClientPlayNetworking.send(new KitSelectionPayload(new BlockLocation(this.blockEntity), List.of(gameType)));
            MinecraftClient.getInstance().setScreen(null);
        }, Text.translatable("button.kit_block.instant_select", gameType.getVariantName().formatted(Formatting.GOLD)))).toList();

        this.selectKitButton = ButtonWidget.builder(Text.translatable("text.kit_block.select_kit"), widget -> {
            ClientPlayNetworking.send(new KitSelectionPayload(new BlockLocation(this.blockEntity), this.selectedGameTypes.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList()));
            MinecraftClient.getInstance().setScreen(null);
        }).dimensions(this.width / 2 - 110, this.height / 2 + 36, 96, 16).build();

        this.itemDisplays = mapEnumerate(this.blockEntity.getKit().getHeldStacks(), (i, stack) -> {
            final int x = this.width / 2 + ITEMS_START_X + (i % 2) * 18;
            final int y = this.height / 2 + ITEMS_START_Y + (i >> 1) * 18;

            return new ItemDisplayWidget(x, y, 16, stack);
        }).toList();
    }
}
