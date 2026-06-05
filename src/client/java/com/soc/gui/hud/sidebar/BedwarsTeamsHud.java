package com.soc.gui.hud.sidebar;

import com.soc.gui.hud.Reference;
import com.soc.gui.hud.SidebarHud;
import com.soc.networking.helper.BedwarsTeam;
import com.soc.networking.helper.PerPlayerBedwarsInfo;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soc.gui.hud.SidebarHud.BACKGROUND_COLOUR;
import static com.soc.gui.hud.SidebarHud.SIDEBAR_WIDTH;
import static net.minecraft.util.math.ColorHelper.lerp;

public class BedwarsTeamsHud extends BaseTeamsHud<BedwarsTeam> {
    public static void initialise() {
        SidebarHud.addHudElement(INSTANCE);
    }

    private static final @NotNull Reference<BedwarsTeamsHud> INSTANCE = new Reference<>();

    private BedwarsTeamsHud(Map<DyeColor, BedwarsTeam> teams) {
        super(teams);
    }

    public static void joinGame(Map<DyeColor, BedwarsTeam> teams) {
        INSTANCE.set(new BedwarsTeamsHud(teams));
    }

    public static void leaveGame() {
        INSTANCE.annul();
    }

    public static void breakBed(DyeColor team) {
        INSTANCE.ifPresent(instance -> instance.teams.get(team).breakBed());
    }

    public static void eliminateTeam(DyeColor team) {
        INSTANCE.ifPresent(instance -> instance.teams.get(team).eliminate());
    }

    @Override
    public void render(DrawContext context, RenderTickCounter renderTickCounter, TextRenderer textRenderer, int x, int y) {
        int i = 0;
        for (DyeColor team : this.teams.keySet()) {
            final int yOrigin = y + 40 * i++;
            this.drawTeamPanel(context, textRenderer, team, x, yOrigin, SIDEBAR_WIDTH, 40);
        }
    }

    protected void drawTeamText(DrawContext context, DyeColor team, TextRenderer textRenderer, int x, int y) {
        final boolean hasBed = this.teams.get(team).hasBed();

        final Text teamBaseString = Text.translatable("hud.bedwars.team", Text.translatable("color.minecraft." + team.asString()));
        context.drawText(textRenderer, teamBaseString, x + 8, y + 4, 0xffffffff, true);

        final Text hasBedBaseString = Text.translatable("hud.bedwars.has_bed");
        context.drawText(textRenderer, hasBedBaseString, x + 8 + textRenderer.getWidth(teamBaseString), y + 4, 0xffffffff, true);

        context.drawText(textRenderer, Text.translatable(hasBed ? "hud.tick" : "hud.cross"), x + 8 + textRenderer.getWidth(hasBedBaseString) + textRenderer.getWidth(teamBaseString), y + 4, hasBed ? 0xff11ee22 : 0xffee1122, true);
    }

    @Override
    public int getSize() {
        return this.teams.keySet().size() * 40;
    }
}
