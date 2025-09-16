package com.soc.gui.screen;

import com.soc.blocks.blockentities.MapBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.gui.widget.NumberTextFieldWidget;
import com.soc.lib.InfoList;
import com.soc.networking.c2s.MapBlockSaveMapPayload;
import com.soc.networking.c2s.MapBlockUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static com.soc.game.map.AbstractGameMap.getMapDirectory;

public class MapBlockScreen extends Screen {
    private final MapBlockEntity blockEntity;
    private final World world;
    private boolean initialised = false;

    private final BlockPos.Mutable regionSize;
    private String mapName;
    private GameType mapType;
    private InfoList mapCheckInfo = new InfoList();

    private TextFieldWidget mapNameTextField;
    private TextFieldWidget sizeXField;
    private TextFieldWidget sizeYField;
    private TextFieldWidget sizeZField;
    private CyclingButtonWidget<GameType> mapTypeButton;
    private ButtonWidget checkStructureButton;
    private ButtonWidget saveStructureButton;
    private ButtonWidget confirmSaveStructureButton;
    private boolean confirmSaveStructure;
    private ButtonWidget openFolderButton;
    private ButtonWidget closeButton;

    public MapBlockScreen(MapBlockEntity blockEntity, World world) {
        super(Text.translatable("screen.map_block_screen"));
        this.blockEntity = blockEntity;
        this.world = world;

        this.regionSize = blockEntity.getRegionSize().mutableCopy();
        this.mapName = blockEntity.getMapName();
        this.mapType = blockEntity.getMapType();
    }

    private void createWidgets() {
        //region Map Name Field
        this.mapNameTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 40, 300, 20, Text.translatable("text.map_block.enter_name")) {
            @Override
            public boolean charTyped(char chr, int modifiers) {
                if (!(Character.isLetterOrDigit(chr) ||"_- .".contains(String.valueOf(chr)))) return false;

                return super.charTyped(chr, modifiers);
            }
        };
        this.mapNameTextField.setMaxLength(64);
        this.mapNameTextField.setText(this.mapName);
        this.mapNameTextField.setChangedListener(string -> this.mapName = string);
        this.addSelectableChild(this.mapNameTextField);
        //endregion
        //region Region Size Fields
        this.sizeXField = new NumberTextFieldWidget(this.textRenderer, this.width / 2 - 152, 80, 60, 20, Text.translatable("text.map_block.x_size_field"), 2000, this.regionSize::setX);
        this.sizeXField.setText(String.valueOf(this.blockEntity.getRegionSize().getX()));
        this.sizeXField.setEditableColor(MapBlockEntity.X_COLOUR);
        this.addSelectableChild(this.sizeXField);

        this.sizeYField = new NumberTextFieldWidget(this.textRenderer, this.width / 2 - 92, 80, 60, 20, Text.translatable("text.map_block.y_size_field"), this.blockEntity.getWorld().getTopYInclusive() - this.blockEntity.getPos().getY(), this.regionSize::setY);
        this.sizeYField.setText(String.valueOf(this.blockEntity.getRegionSize().getY()));
        this.sizeYField.setEditableColor(MapBlockEntity.Y_COLOUR);
        this.addSelectableChild(this.sizeYField);

        this.sizeZField = new NumberTextFieldWidget(this.textRenderer, this.width / 2 - 32, 80, 60, 20, Text.translatable("text.map_block.z_size_field"), 2000, this.regionSize::setZ);
        this.sizeZField.setText(String.valueOf(this.blockEntity.getRegionSize().getZ()));
        this.sizeZField.setEditableColor(MapBlockEntity.Z_COLOUR);
        this.addSelectableChild(this.sizeZField);
        //endregion
        //region Map Type Cycler
        this.mapTypeButton = this.addDrawableChild(CyclingButtonWidget.builder(GameType::getVariantName)
                .values(GameType.values()).omitKeyText().initially(this.mapType)
                .build(this.width / 2 - 152, 120, 100, 20, Text.translatable("button.map_block.game_type"), (button, mapType) -> {
                    this.mapType = mapType;
                    this.refreshMapCheckInfo();
                }));
        //endregion
        //region Structure Buttons
        this.checkStructureButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.check_structure"), button -> {
            this.saveAndSync();
            this.blockEntity.checkStructure();
            this.refreshMapCheckInfo();

            this.confirmSaveStructure = false;
            this.init();
        }).dimensions(this.width / 2 + 38, 80, 110, 20).build());

        this.saveStructureButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.save_structure"), button -> {
            this.saveAndSync();
            this.blockEntity.checkStructure();
            if (this.mapCheckInfo.hasWarnings()) {
                this.confirmSaveStructure = true;
                this.init();
            } else if (!this.mapCheckInfo.hasErrors()) {
                this.doServerMapSave();
            }
        }).dimensions(this.width / 2 - 152, 145, 100, 20).build());
        this.confirmSaveStructureButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.confirm_save_structure").formatted(Formatting.RED), button -> {
            this.saveAndSync();
            this.confirmSaveStructure = false;
            this.init();
            this.doServerMapSave();
        }).dimensions(this.width / 2 - 152, 145, 100, 20).build());
        //endregion
        //region Close Button
        this.openFolderButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.open_folder"), button -> {
            Util.getOperatingSystem().open(getMapDirectory());
        }).dimensions(this.width / 2 - 152, 185, 100, 20).build());
        this.closeButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.close"), button -> {
            this.saveSyncClose();
        }).dimensions(this.width / 2 - 152, 210, 100, 20).build());
        //endregion
    }

    @Override
    protected void init() {
        if (!initialised) {
            this.createWidgets();
            initialised = true;
        }

        this.mapNameTextField.setPosition(this.width / 2 - 152, 40);
        this.addDrawableChild(this.mapNameTextField);

        this.sizeXField.setPosition(this.width / 2 - 152, 80);
        this.addDrawableChild(this.sizeXField);
        this.sizeYField.setPosition(this.width / 2 - 92, 80);
        this.addDrawableChild(this.sizeYField);
        this.sizeZField.setPosition(this.width / 2 - 32, 80);
        this.addDrawableChild(this.sizeZField);

        this.mapTypeButton.setPosition(this.width / 2 - 152, 120);
        this.addDrawableChild(this.mapTypeButton);

        this.checkStructureButton.setPosition(this.width / 2 + 38, 80);
        this.addDrawableChild(this.checkStructureButton);
        this.saveStructureButton.setPosition(this.width / 2 - 152, 145);
        this.confirmSaveStructureButton.setPosition(this.width / 2 - 152, 145);
        this.addDrawableChild(this.confirmSaveStructure ? this.confirmSaveStructureButton : this.saveStructureButton);
        this.saveStructureButton.visible = !this.confirmSaveStructure;
        this.confirmSaveStructureButton.visible = this.confirmSaveStructure;

        this.openFolderButton.setPosition(this.width / 2 - 152, 185);
        this.addDrawableChild(this.openFolderButton);
        this.closeButton.setPosition(this.width / 2 - 152, 210);
        this.addDrawableChild(this.closeButton);
    }

    /*
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) {
            if (modifiers == 1) {
                this.saveSyncClose();
            } else {
                this.saveAndSync();
            }
            return false;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
     */

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, -1);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.enter_name_field"), this.width / 2 - 153, 30, -6250336);
        this.mapNameTextField.render(context, mouseX, mouseY, deltaTicks);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.enter_x_field"), this.width / 2 - 153, 70, MapBlockEntity.X_COLOUR);
        this.sizeXField.render(context, mouseX, mouseY, deltaTicks);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.enter_y_field"), this.width / 2 - 93, 70, MapBlockEntity.Y_COLOUR);
        this.sizeYField.render(context, mouseX, mouseY, deltaTicks);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.enter_z_field"), this.width / 2 - 33, 70, MapBlockEntity.Z_COLOUR);
        this.sizeZField.render(context, mouseX, mouseY, deltaTicks);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.game_type"), this.width / 2 - 153, 110, -6250336);

        this.saveStructureButton.active = !this.mapCheckInfo.hasErrors();

        List<Pair<Text, Text>> warnings = this.mapCheckInfo.getInfo();
        context.fill(this.width / 2 - 32, 110, this.width / 2 + 148, 110 + 10 * warnings.size() + 8, 0xff000000);
        context.drawBorder(this.width / 2 - 32, 110, 180, 10 * warnings.size() + 8, -6250336);
        for (int i = 0; i < warnings.size(); i++) {
            context.drawTextWithShadow(this.textRenderer, warnings.get(i).getLeft(), this.width / 2 - 27, 115 + 10 * i, -6250336);
        }
    }

    private void doServerMapSave() {
        this.client.setScreen(null);
        ClientPlayNetworking.send(new MapBlockSaveMapPayload(this.blockEntity.getPos().asLong()));
    }

    private void saveSyncClose() {
        this.saveAndSync();
        super.client.setScreen(null);
    }

    private void saveAndSync() {
        this.blockEntity.setRegionSize(this.regionSize);
        this.blockEntity.setMapName(this.mapName);
        this.blockEntity.setMapType(this.mapType);

        final MapBlockUpdatePayload payload = new MapBlockUpdatePayload(this.blockEntity.getPos().asLong(), this.regionSize.asLong(), this.mapName, this.mapType.ordinal());
        ClientPlayNetworking.send(payload);
    }

    private void refreshMapCheckInfo() {
        this.mapCheckInfo = this.blockEntity.getMapCheckInfo(this.mapType);
    }
}
