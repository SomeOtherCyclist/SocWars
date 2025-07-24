package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.tools.ToolMaterials;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static com.soc.items.util.ModItems.addItemToGroups;

public class Dashrend extends Item {
    public Dashrend(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        addItemToGroups(DASHREND, ItemGroups.COMBAT);
    }

    public static final Item DASHREND = ModItems.register("dashrend", Dashrend::new, new Settings()
            .useCooldown(3.5f)
            .sword(ToolMaterials.DASH_TOOL_MATERIAL, 2, -2)
            .component(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(false, ReferenceSortedSets.emptySet()))
    );

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {

        float pitchClosenessToHorizontal = 1f - Math.abs(user.getPitch() / 90f);
        float pitchStrength = pitchClosenessToHorizontal * 0.5f + 0.5f;
        float dashStrength = (float) Math.sqrt(pitchStrength) * (user.isOnGround() ? 2f : 0.75f) * 0.5f;

        user.addVelocity(user.getRotationVector().multiply(dashStrength));

        ItemStack item = user.getStackInHand(hand);
        item.setDamage(item.getDamage() + Math.round(pitchStrength * 10));

        if (item.getDamage() == item.getMaxDamage()) {
            user.setStackInHand(hand, ItemStack.EMPTY);
            user.playSound(SoundEvent.of(Identifier.of("minecraft:entity.item.break")));
        }

        return ActionResult.SUCCESS;
    }
}
