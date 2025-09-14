package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.blocks.blockentities.MapBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.gui.widget.NumberTextFieldWidget;
import com.soc.networking.c2s.MapBlockUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MapBlockScreen extends Screen {
    private final MapBlockEntity blockEntity;
    private final World world;

    private final BlockPos.Mutable regionSize;
    private String mapName;
    private GameType mapType;

    private TextFieldWidget mapNameTextField;
    private TextFieldWidget sizeXField;
    private TextFieldWidget sizeYField;
    private TextFieldWidget sizeZField;
    private CyclingButtonWidget<GameType> mapTypeButton;
    private ButtonWidget saveButton;
    private ButtonWidget checkStructureButton;

    public MapBlockScreen(MapBlockEntity blockEntity, World world) {
        super(Text.translatable("screen.map_block_screen"));
        this.blockEntity = blockEntity;
        this.world = world;

        this.regionSize = blockEntity.getRegionSize().mutableCopy();
        this.mapName = blockEntity.getMapName();
        this.mapType = blockEntity.getMapType();
    }

    @Override
    protected void init() {
        //region Map Name
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
        //region Region X Field
        this.sizeXField = new NumberTextFieldWidget(this.textRenderer, this.width / 2 - 152, 80, 80, 20, Text.translatable("text.map_block.x_size_field"), 2000, this.regionSize::setX);
        this.sizeXField.setText(String.valueOf(this.blockEntity.getRegionSize().getX()));
        this.sizeXField.setEditableColor(MapBlockEntity.X_COLOUR);
        this.addSelectableChild(this.sizeXField);
        //endregion
        //region Region Y Field
        this.sizeYField = new NumberTextFieldWidget(this.textRenderer, this.width / 2 - 62, 80, 80, 20, Text.translatable("text.map_block.y_size_field"), this.blockEntity.getWorld().getTopYInclusive() - this.blockEntity.getPos().getY(), this.regionSize::setY);
        this.sizeYField.setText(String.valueOf(this.blockEntity.getRegionSize().getY()));
        this.sizeYField.setEditableColor(MapBlockEntity.Y_COLOUR);
        this.addSelectableChild(this.sizeYField);
        //endregion
        //region Region Z Field
        this.sizeZField = new NumberTextFieldWidget(this.textRenderer, this.width / 2 + 28, 80, 80, 20, Text.translatable("text.map_block.z_size_field"), 2000, this.regionSize::setZ);
        this.sizeZField.setText(String.valueOf(this.blockEntity.getRegionSize().getZ()));
        this.sizeZField.setEditableColor(MapBlockEntity.Z_COLOUR);
        this.addSelectableChild(this.sizeZField);
        //endregion
        //region Map Type Cycler
        this.mapTypeButton = this.addDrawableChild(CyclingButtonWidget.builder(GameType::getVariantName)
                .values(GameType.values()).omitKeyText().initially(this.mapType)
                .build(this.width / 2 - 152, 120, 80, 20, Text.translatable("button.map_block.game_type"), (button, mapType) -> this.mapType = mapType));
        //endregion
        //region Save Button
        this.saveButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.save"), button -> {
            this.saveSyncClose();
        }).dimensions(this.width / 2 - 152, 160, 80, 20).build());
        //endregion
        //region Check Structure Button
        this.checkStructureButton = super.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.check_structure"), button -> {
            this.saveAndSync();
            this.blockEntity.checkStructure(); //This should be performed on the server and have results sent back to the client
        }).dimensions(this.width / 2 - 152, 200, 80, 20).build());
        //endregion
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

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.enter_y_field"), this.width / 2 - 63, 70, MapBlockEntity.Y_COLOUR);
        this.sizeYField.render(context, mouseX, mouseY, deltaTicks);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.enter_z_field"), this.width / 2 + 27, 70, MapBlockEntity.Z_COLOUR);
        this.sizeZField.render(context, mouseX, mouseY, deltaTicks);

        context.drawTextWithShadow(this.textRenderer, Text.translatable("text.map_block.game_type"), this.width / 2 - 153, 110, -6250336);
    }

    private void saveSyncClose() {
        super.client.setScreen(null);
        saveAndSync();
    }

    private void saveAndSync() {
        this.blockEntity.setRegionSize(this.regionSize);
        this.blockEntity.setMapName(this.mapName);
        this.blockEntity.setMapType(this.mapType);

        MapBlockUpdatePayload payload = new MapBlockUpdatePayload(this.blockEntity.getPos().asLong(), this.world.getRegistryKey(), this.regionSize.asLong(), this.mapName, this.mapType.ordinal());
        ClientPlayNetworking.send(payload);
    }
}
