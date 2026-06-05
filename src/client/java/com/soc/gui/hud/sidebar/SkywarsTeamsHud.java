package com.soc.gui.hud.sidebar;

import com.soc.gui.hud.Reference;
import com.soc.gui.hud.SidebarHud;
import com.soc.networking.helper.SkywarsTeam;
import com.soc.networking.s2c.skywars.SetTeamLivesPayload;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.Comparator;
import java.util.Map;

import static com.soc.gui.hud.SidebarHud.BACKGROUND_COLOUR;
import static com.soc.gui.hud.SidebarHud.SIDEBAR_WIDTH;
import static net.minecraft.util.math.ColorHelper.lerp;

public class SkywarsTeamsHud extends BaseTeamsHud<SkywarsTeam> { //Maybe I'll subclass this and BedwarsTeamsHud under GameTeamsHud or something and make a lof of the drawing code common //I did this it was good
    public static void initialise() {
        SidebarHud.addHudElement(INSTANCE);
    }

    private static final @NotNull Reference<SkywarsTeamsHud> INSTANCE = new Reference<>();

    private static final Identifier FULL_HEART_NORMAL = Identifier.ofVanilla("hud/heart/full");
    private static final Identifier FULL_HEART_HARDCORE = Identifier.ofVanilla("hud/heart/hardcore_full");

    private static final boolean STRETCH_LAST_BLOCK = false; //May make this toggleable at some point

    private SkywarsTeamsHud(Map<DyeColor, SkywarsTeam> teams) {
        super(teams);
    }

    public static void joinGame(Map<DyeColor, SkywarsTeam> teams) {
        INSTANCE.set(new SkywarsTeamsHud(teams));
    }

    public static void leaveGame() {
        INSTANCE.annul();
    }

    public static void eliminateTeam(DyeColor team) {
        INSTANCE.ifPresent(instance -> instance.teams.get(team).eliminate());
    }

    public static void setTeamLives(SetTeamLivesPayload payload) {
        INSTANCE.ifPresent(instance -> instance.teams.get(payload.team()).setLives(payload.lives()));
    }

    @Override
    public void render(DrawContext context, RenderTickCounter renderTickCounter, TextRenderer textRenderer, int x, int y) {
        final int halfSidebarWidth = SIDEBAR_WIDTH >> 1;

        int i = 0;
        for (DyeColor team : this.teams.entrySet().stream().sorted(Comparator.comparingInt(entry -> -entry.getValue().getLives())).map(Map.Entry::getKey).toList()) {
            final boolean isIOddAndLast = i % 2 == 0 && i == this.teams.size() - 1 && STRETCH_LAST_BLOCK;

            final int xOrigin = x + halfSidebarWidth * (i % 2);
            final int yOrigin = y + 40 * (i++ >> 1);

            this.drawTeamPanel(context, textRenderer, team, xOrigin, yOrigin, isIOddAndLast ? halfSidebarWidth * 3 / 2 : halfSidebarWidth, 40);
        }
    }

    protected void drawTeamText(DrawContext context, DyeColor team, TextRenderer textRenderer, int x, int y) {
        super.drawTeamText(context, team, textRenderer, x, y);

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FULL_HEART_NORMAL, x + 46, y + 18, 13, 13);

        final Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.scaleAround(1.5f, x + 36, y + 20);

        context.drawText(textRenderer, String.valueOf(this.teams.get(team).getLives()), x + 36, y + 20, 0xffffffff, true);

        matrices.popMatrix();
    }

    @Override
    public int getSize() {
        return ((this.teams.size() + 1) >> 1) * 40;
    }
}
