package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.game.Kit;
import com.soc.game.manager.GameType;
import com.soc.gui.widget.NumberTextFieldWidget;
import com.soc.gui.widget.ToggleButtonWidget;
import com.soc.networking.c2s.KitBlockUpdatePayload;
import com.soc.networking.helper.BlockLocation;
import com.soc.screenhandler.KitBlockCreationScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.soc.gui.screen.KitBlockSelectionScreen.TEXTURES;
import static com.soc.lib.SocWarsLib.*;

public class KitBlockCreationScreen extends HandledScreen<KitBlockCreationScreenHandler> {
    public static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/kit_block_creation.png");

    private boolean initialised;

    private Map<GameType, ToggleButtonWidget> gameSelectionButtons;

    private TextFieldWidget nameField;
    private String name = "";
    private ButtonWidget renameButton;

    private TextFieldWidget costScoreboardField;
    private NumberTextFieldWidget costField;

    public KitBlockCreationScreen(KitBlockCreationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 208;
        this.backgroundHeight = 302;
        this.titleY = 0;
        this.playerInventoryTitleX = 40;
        this.playerInventoryTitleY = 191;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);

        enumerate(GameType.values(), (i, gameType) -> {
            final MutableText variantName = gameType.getCompactVariantName();
            final boolean enabled = this.handler.getAllowedGameTypesList().contains(gameType);
            variantName.append(Text.translatable(enabled ? "hud.tick" : "hud.cross"));

            context.drawText(this.textRenderer, variantName, this.width / 2 - 92, this.height / 2 - 86 + i * 18, enabled ? 0xff11ee22 : 0xffee1122, true);
        });
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        final int i = (this.width - this.backgroundWidth) / 2;
        final int j = (this.height - this.backgroundHeight) / 2 - 18;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void init() {
        super.init();

        if (!this.initialised) {
            this.createWidgets();
            this.initialised = true;
        }

        enumerate(this.gameSelectionButtons.values(), (i, widget) -> {
            widget.setPosition(this.width / 2 - 95, this.height / 2 - 90 + i * 18);
            this.addDrawableChild(widget);
        });

        this.nameField.setPosition(this.width / 2 - 97, this.height / 2 - 140);
        this.addDrawableChild(this.nameField);
        this.renameButton.setPosition(this.width / 2 + 37, this.height / 2 - 140);
        this.addDrawableChild(this.renameButton);
        this.costScoreboardField.setPosition(this.width / 2 - 33, this.height / 2 - 116);
        this.addDrawableChild(this.costScoreboardField);
        this.costField.setPosition(this.width / 2 - 97, this.height / 2 - 116);
        this.addDrawableChild(this.costField);
    }

    private void createWidgets() {
        this.gameSelectionButtons = mapFromArrayEnumerate(GameType.values(), (i, gameType) -> new ToggleButtonWidget(0, 0, 96, 16, false, isToggled -> {
            this.handler.setGameTypeAllowed(gameType, isToggled);
            this.sync();
        }, TEXTURES));

        this.nameField = new TextFieldWidget(this.textRenderer, 130, 20, Text.empty());
        this.nameField.setChangedListener(name -> {
            KitBlockCreationScreen.this.name = name;
            this.updateNameFieldColour();
        });
        this.renameButton = ButtonWidget.builder(Text.translatable("button.kit_block.rename"), widget -> {
            ifNotNull(KitBlockCreationScreen.this.getKit(), kit -> kit.setName(KitBlockCreationScreen.this.name));
            this.sync();
            this.updateNameFieldColour();
        }).size(60, 20).build();

        this.costScoreboardField = new TextFieldWidget(this.textRenderer, 130, 20, Text.empty());
        this.costScoreboardField.setChangedListener(variableName -> {
            ifNotNull(KitBlockCreationScreen.this.getBlockEntity(), blockEntity -> blockEntity.setScoreboardVariableName(variableName));
            this.sync();
            this.updateVariableFieldColour();
        });
        this.costField = new NumberTextFieldWidget(this.textRenderer, 60, 20, Text.empty(), 1, 99999, i -> ifNotNull(this.getBlockEntity(), blockEntity -> blockEntity.setCost(i)));
    }

    private void updateNameFieldColour() {
		this.nameField.setEditableColor(this.name.equals(mapIfNotNull(this.getKit(), Kit::getName, null)) ? 0xffffffff : 0xffd0e040);
    }

    private void updateVariableFieldColour() {
		this.costScoreboardField.setEditableColor(this.getBlockEntity() == null || this.getBlockEntity().isScoreboardVariableValid() ? 0xffffffff : 0xffd04040);
    }

    private void sync() {
        if (this.getBlockEntity() == null) return;

        ClientPlayNetworking.send(new KitBlockUpdatePayload(
                new BlockLocation(this.handler.getBlockEntity()), this.handler.getAllowedGameTypes(),
                this.getKit(),
                this.getBlockEntity().getCost(),
                this.getBlockEntity().getScoreboardVariableName()
        ));
    }

    public void setBlockEntity(KitBlockEntity blockEntity) {
        this.getScreenHandler().setBlockEntity(blockEntity);

        for (GameType gameType : GameType.values()) {
            this.gameSelectionButtons.get(gameType).setToggled(blockEntity.allowsGameType(gameType));
        }

        this.nameField.setText(blockEntity.getKit().getName());
        this.costField.setText(String.valueOf(blockEntity.getCost()));
        this.costScoreboardField.setText(blockEntity.getScoreboardVariableName());
    }

    @Nullable
    private KitBlockEntity getBlockEntity() {
        return this.getScreenHandler().getBlockEntity();
    }

    @Nullable
    private Kit getKit() {
        return propogateNull(this.getBlockEntity(), KitBlockEntity::getKit);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { //I don't like this solution because it looks gross as hell but it seems to work. Wish I could just do Screen.super.keyPressed
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            return this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers);
        } else return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

