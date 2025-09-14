package com.soc.screens;

import com.soc.blocks.blockentities.MapBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MapBlockScreen extends Screen {
    private ButtonWidget buttonSave;

    public MapBlockScreen(MapBlockEntity mapBlockEntity) {
        super(Text.translatable("screen.map_block_screen"));
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);

        this.initialise(client, width, height);
    }

    private void initialise(MinecraftClient client, int width, int height) {
        this.buttonSave = this.addDrawableChild(ButtonWidget.builder(Text.translatable("button.map_block.save"), (button) -> {
            client.setScreen(null);
        }).dimensions(width / 2 + 4 + 100, 185, 50, 20).build());
    }
}
