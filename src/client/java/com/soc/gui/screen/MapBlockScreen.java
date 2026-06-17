package com.soc.gui.screen;

import com.soc.blocks.blockentities.MapBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.game.map.Enabled;
import com.soc.game.map.RangedIntField;
import com.soc.gui.widget.NumberTextFieldWidget;
import com.soc.lib.InfoList;
import com.soc.networking.c2s.MapBlockSaveMapPayload;
import com.soc.networking.c2s.MapBlockUpdatePayload;
import com.soc.networking.helper.BlockLocation;
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
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.soc.game.map.AbstractGameMap.getMapDirectory;
import static com.soc.lib.SocWarsLib.ifNotNull;

public class MapBlockScreen extends Screen {
    private final MapBlockEntity blockEntity;
    private boolean initialised = false;

    private final BlockPos.Mutable regionSize;
    private String mapName;
    private GameType mapType;
    private boolean blockProtection;

    private InfoList mapCheckInfo;

    private TextFieldWidget mapNameTextField;
    private TextFieldWidget sizeXField;
    private TextFieldWidget sizeYField;
    private TextFieldWidget sizeZField;
    private CyclingButtonWidget<GameType> mapTypeButton;
    private ButtonWidget checkStructureButton;
    private ButtonWidget saveStructureButton;
    private ButtonWidget confirmSaveStructureButton;
    private boolean confirmSaveStructure;
    private CyclingButtonWidget<Enabled> blockProtectionButton;
    private ButtonWidget openFolderButton;
    private ButtonWidget closeButton;

    private final Map<GameType, List<NumberTextFieldWidget>> optionalFields = new HashMap<>();
    private final Map<String, Integer> fields;

    public MapBlockScreen(MapBlockEntity blockEntity) {
        super(Text.translatable("screen.map_block"));
        this.blockEntity = blockEntity;

        this.regionSize = blockEntity.getRegionSize().mutableCopy();
        this.mapName = blockEntity.getMapName();
        this.mapType = blockEntity.getMapType();
        this.blockProtection = blockEntity.hasBlockProtection();
        this.fields = blockEntity.getFields();

        this.mapCheckInfo = blockEntity.getMapCheckInfo(this.mapType);
    }

    private void createWidgets() {
        //region Map Name Field
        this.mapNameTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 40, 304, 20, Text.translatable("text.map_block.enter_name")) {
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
        this.sizeXField = new NumberTextFieldWidget(this.textRenderer, this.width / 2 - 152, 80, 60, 20, Text.translatable("text.map_block.x_size_field"), 1, 2000, this.regionSize::setX);
        this.sizeXField.setText(String.valueOf(this.blockEntity.getRegionSize().getX()));
        this.sizeXField.setEditableColor(MapBlockEntity.X_COLOUR);
        this.addSelectableChild(this.sizeXField);

        this.sizeYField = new NumberTextFieldWidget(this.textRenderer, this.width / 2 - 92, 80, 60, 20, Text.translatable("text.map_block.y_size_field"), 1, this.blockEntity.getWorld().getTopYInclusive() - this.blockEntity.getPos().getY(), this.regionSize::setY);
        this.sizeYField.setText(String.valueOf(this.blockEntity.getRegionSize().getY()));
        this.sizeYField.setEditableColor(MapBlockEntity.Y_COLOUR);
        this.addSelectableChild(this.sizeYField);

        this.sizeZField = new NumberTextFieldWidget(this.textRenderer, this.width / 2 - 32, 80, 60, 20, Text.translatable("text.map_block.z_size_field"), 1, 2000, this.regionSize::setZ);
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
                    this.refreshOptionalFields();
                })
        );
        //endregion
        //region Structure Buttons
        this.checkStructureButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.check_structure"), button -> {
            this.saveAndSync();
            this.blockEntity.checkStructure();
            this.refreshMapCheckInfo();

            this.confirmSaveStructure = false;
            this.init();
        }).dimensions(this.width / 2 + 38, 80, 114, 20).build());
        this.blockProtectionButton = this.addDrawableChild(CyclingButtonWidget.builder(Enabled::getVariantName)
                .values(Enabled.values()).omitKeyText().initially(Enabled.fromBoolean(this.blockProtection))
                .build(this.width / 2 - 152, 155, 100, 20, Text.translatable("button.map_block.block_protection"), (button, value) -> {
                    this.blockProtection = value.booleanValue();
                    this.saveAndSync();
                })
        );

        this.saveStructureButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.save_structure"), button -> {
            this.saveAndSync();
            this.blockEntity.checkStructure();
            if (this.mapCheckInfo.hasWarnings()) {
                this.confirmSaveStructure = true;
                this.init();
            } else if (!this.mapCheckInfo.hasErrors()) {
                this.doServerMapSave();
            }
        }).dimensions(this.width / 2 - 152, 195, 100, 20).build());
        this.confirmSaveStructureButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.confirm_save_structure").formatted(Formatting.RED), button -> {
            this.saveAndSync();
            this.confirmSaveStructure = false;
            this.init();
            this.doServerMapSave();
        }).dimensions(this.width / 2 - 152, 195, 100, 20).build());
        //endregion
        //region Open Folder Button
        this.openFolderButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.open_folder"), button -> {
            Util.getOperatingSystem().open(getMapDirectory());
        }).dimensions(this.width / 2 - 152, 235, 100, 20).build());
        //endregion
        //region Close Button
        this.closeButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.close"), button -> {
            this.saveSyncClose();
        }).dimensions(this.width / 2 - 152, 260, 100, 20).build());
        //endregion

        this.refreshOptionalFields();
    }

    @Override
    protected void init() {
        if (!this.initialised) {
            this.createWidgets();
            this.initialised = true;
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
        this.blockProtectionButton.setPosition(this.width / 2 - 152, 155);
        this.addDrawableChild(this.blockProtectionButton);
        this.saveStructureButton.setPosition(this.width / 2 - 152, 195);
        this.confirmSaveStructureButton.setPosition(this.width / 2 - 152, 195);
        this.addDrawableChild(this.confirmSaveStructure ? this.confirmSaveStructureButton : this.saveStructureButton);
        this.saveStructureButton.visible = !this.confirmSaveStructure;
        this.confirmSaveStructureButton.visible = this.confirmSaveStructure;

        this.openFolderButton.setPosition(this.width / 2 - 152, 235);
        this.addDrawableChild(this.openFolderButton);
        this.closeButton.setPosition(this.width / 2 - 152, 260);
        this.addDrawableChild(this.closeButton);

        final List<NumberTextFieldWidget> widgets = this.optionalFields.get(this.mapType);
        for (int i = 0; i < widgets.size(); i++) {
            final int x = (i % 3) * 105 + this.width / 2 - 152;
            final int y = ((int)Math.floor(i / 3f)) * 40 + 300;

            widgets.get(i).setPosition(x, y);
            this.addDrawableChild(widgets.get(i));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) {
            this.saveAndSync();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void refreshOptionalFields() {
        this.optionalFields.forEach((mapType, widgets) -> {
            if (mapType != this.mapType) for (NumberTextFieldWidget widget : widgets) {
                widget.active = false;
                widget.visible = false;
            }
        });
        this.optionalFields.computeIfAbsent(this.mapType, this::buildOptionalFields);
        for (NumberTextFieldWidget numberTextFieldWidget : this.optionalFields.get(this.mapType)) {
            numberTextFieldWidget.active = true;
            numberTextFieldWidget.visible = true;
        }
    }

    private List<NumberTextFieldWidget> buildOptionalFields(GameType mapType) {
        final List<NumberTextFieldWidget> list = new ArrayList<>(mapType.getMapFields().size());

        final Iterator<RangedIntField> fields = mapType.getMapFields().values().iterator();
        for (int i = 0; fields.hasNext(); i++) {
            final RangedIntField field = fields.next();

            final int x = (i % 3) * 105 + this.width / 2 - 152;
            final int y = ((int)Math.floor(i / 3f)) * 40 + 300;

            final NumberTextFieldWidget widget = this.addDrawableChild(new NumberTextFieldWidget(this.textRenderer, x, y, 94, 20, Text.translatable("text.map_block.field." + field.name()), field.minValue(), field.maxValue(), value -> {
                this.fields.put(field.name(), value);
            }));

            ifNotNull(this.fields.get(field.name()), value -> widget.setText(String.valueOf(value)));

            list.add(widget);
        }

        return list;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xffffffff);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.enter_name_field"), this.width / 2 - 153, 30, 0xffa0a0a0);
        this.mapNameTextField.render(context, mouseX, mouseY, deltaTicks);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.enter_x_field"), this.width / 2 - 153, 70, MapBlockEntity.X_COLOUR);
        this.sizeXField.render(context, mouseX, mouseY, deltaTicks);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.enter_y_field"), this.width / 2 - 93, 70, MapBlockEntity.Y_COLOUR);
        this.sizeYField.render(context, mouseX, mouseY, deltaTicks);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.enter_z_field"), this.width / 2 - 33, 70, MapBlockEntity.Z_COLOUR);
        this.sizeZField.render(context, mouseX, mouseY, deltaTicks);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.game_type"), this.width / 2 - 153, 110, 0xffa0a0a0);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.block_protection"), this.width / 2 - 153, 145, 0xffa0a0a0);

        this.saveStructureButton.active = !this.mapCheckInfo.hasErrors();

        //region
        final List<Pair<Text, Text[]>> warnings = this.mapCheckInfo.getInfo();

        final int infoStartX = this.width / 2 - 32;
        final int infoStartY = 110;
        final int infoWidth = 184;
        final int infoTextPadding = 5;
        final int infoTextHeight = 10;
        final int infoHeight = infoTextHeight * warnings.size() + 8;

        context.fill(infoStartX, infoStartY, infoStartX + infoWidth, infoStartY + infoHeight, 0xff000000);
        context.drawBorder(infoStartX, infoStartY, infoWidth, infoHeight, 0xffa0a0a0);
        for (int i = 0; i < warnings.size(); i++) {
            context.drawTextWithShadow(this.textRenderer, warnings.get(i).getLeft(), infoStartX + infoTextPadding, infoStartY + infoTextPadding + i * infoTextHeight, -6250336);
        }

        if (mouseX > infoStartX && mouseX < infoStartX + infoWidth && mouseY > infoStartY && mouseY < infoStartY + infoHeight && !warnings.isEmpty()) {
            final int index = Math.min((mouseY - infoStartY - infoTextPadding + 1) / infoTextHeight, warnings.size() - 1);
            final Text[] hoverText = warnings.get(index).getRight();
            for (int i = 0; i < hoverText.length; i++) {
                context.drawTextWithShadow(this.textRenderer, hoverText[i], mouseX + 6, mouseY + i * infoTextHeight, 0xffbfbfbf);
            }
        }

        this.optionalFields.get(this.mapType).forEach(widget -> {
            context.drawTextWithShadow(this.textRenderer, widget.getMessage(), widget.getX() - 1, widget.getY() - 10, 0xffbfbfbf);
        });
    }

    private void doServerMapSave() {
        this.client.setScreen(null);
        this.saveAndSync();
        ClientPlayNetworking.send(new MapBlockSaveMapPayload(new BlockLocation(this.blockEntity)));
    }

    private void saveSyncClose() {
        this.saveAndSync();
        super.client.setScreen(null);
    }

    private void saveAndSync() {
        this.blockEntity.setRegionSize(this.regionSize);
        this.blockEntity.setMapName(this.mapName);
        this.blockEntity.setMapType(this.mapType);
        this.blockEntity.setBlockProtection(this.blockProtection);

        final MapBlockUpdatePayload payload = new MapBlockUpdatePayload(new BlockLocation(this.blockEntity), this.regionSize.asLong(), this.mapName, this.mapType.ordinal(), this.blockProtection, this.fields);
        ClientPlayNetworking.send(payload);
    }

    private void refreshMapCheckInfo() {
        this.mapCheckInfo = this.blockEntity.getMapCheckInfo(this.mapType);
    }
}