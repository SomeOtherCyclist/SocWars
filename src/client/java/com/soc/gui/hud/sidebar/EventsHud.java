package com.soc.gui.hud.sidebar;

import com.soc.game.manager.Event;
import com.soc.gui.hud.Reference;
import com.soc.gui.hud.SidebarHud;
import com.soc.gui.hud.VerticallyStackedHudComponent;
import com.soc.networking.s2c.EventQueuePayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.soc.gui.hud.SidebarHud.SIDEBAR_WIDTH;
import static com.soc.lib.SocWarsLib.getTimeFromSeconds;

public class EventsHud implements VerticallyStackedHudComponent {
    public static final @NotNull Reference<EventsHud> INSTANCE = new Reference<>();

    public static void initialise() {
        SidebarHud.addHudElement(INSTANCE);
    }

    private final List<Event.ClientDisplayEvent> events;
    private final long startTime;

    public EventsHud(List<Event.ClientDisplayEvent> events) {
        this.events = events; //Pretty sure I used a mutable list in the codec but I guess I'll find out soon
        this.startTime = MinecraftClient.getInstance().world.getTime();
    }

    public static void receivePayload(EventQueuePayload payload) {
        INSTANCE.set(new EventsHud(payload.events()));
    }

    public static void clear() {
        INSTANCE.annul();
    }

    @Override
    public int getSize() {
        return 46;
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter, TextRenderer textRenderer, int x, int y) {
        final Text title = Text.translatable("hud.events");
        drawContext.drawText(textRenderer, title, x + (SIDEBAR_WIDTH - textRenderer.getWidth(title) >> 1), y + 4, 0xffffffff, true);

        long time = MinecraftClient.getInstance().world.getTime();
        if (!this.events.isEmpty() && this.events.getFirst().time() + this.startTime <= time) this.events.removeFirst();

        for (int i = 0; i < 2; i++) {
            if (i < this.events.size()) {
                final Event.ClientDisplayEvent event = this.events.get(i);
                final Text text = Text.translatable("hud.upcoming_event", event.name(), getTimeFromSeconds((event.time() + (this.startTime - time)) * 0.05f, false, SidebarHud.TIME_COLOURS));
                drawContext.drawText(textRenderer, text, x + 8, y + 18 + i * 14, 0xffffffff, true);
            }
        }
    }
}
