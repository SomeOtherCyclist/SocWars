package com.soc.gui.hud;

import com.soc.SocWars;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SidebarHud {
    public static final int BACKGROUND_COLOUR = 0x38000000;
    public static final int SIDEBAR_WIDTH = 128;
    private static final int TIME_COLOUR = Objects.requireNonNull(Formatting.DARK_GREEN.getColorValue()) | 0xff000000;
    public static final int[] TIME_COLOURS = {TIME_COLOUR, TIME_COLOUR};

    public static void initialise() {
        HudElementRegistry.addFirst(Identifier.of(SocWars.MOD_ID, "sidebar_hud"), SidebarHud::render);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ELEMENTS.forEach(Reference::annul));
    }

    private static final List<@NotNull Reference<? extends VerticallyStackedHudComponent>> ELEMENTS = new ArrayList<>();

    private SidebarHud() {}

    public static void addHudElement(@NotNull Reference<? extends VerticallyStackedHudComponent> element) {
        ELEMENTS.add(element);
    }

    private static void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        final List<? extends VerticallyStackedHudComponent> activeElements = ELEMENTS.stream().map(Reference::get).filter(Objects::nonNull).sorted(Comparator.comparingInt(component -> -component.priority())).toList(); //I don't like sorting every frame but it's the best idea I can think of right now
        if (activeElements.isEmpty()) return;

        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        final int width = drawContext.getScaledWindowWidth();
        final int height = drawContext.getScaledWindowHeight();
        final int elementsHeigtht = activeElements.stream().mapToInt(VerticallyStackedHudComponent::getSize).sum();

        final int topY = (height - elementsHeigtht) >> 1;
        drawContext.fill(width - SIDEBAR_WIDTH, topY, width, topY + elementsHeigtht, BACKGROUND_COLOUR);

        int y = topY; //Mutable copy because I don't have myself enough to debug it when I inevitably screw something up
        for (VerticallyStackedHudComponent component : activeElements) {
            component.render(drawContext, renderTickCounter, textRenderer, width - 128, y);
            y += component.getSize();
        }
    }
}
