package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.items.util.StopUsingFunction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DrawableWeapon extends Item {
    private final StopUsingFunction stopUsingFunction;

    public DrawableWeapon(Settings settings, StopUsingFunction stopUsingFunction) {
        super(settings);
        this.stopUsingFunction = stopUsingFunction;
    }

    public static void initialise() {
        ModItems.addItemToGroups(ENDER_STAFF, ItemGroups.COMBAT);
    }

    public static final Item ENDER_STAFF = ModItems.register("ender_staff", settings -> new DrawableWeapon(settings, (stack, world, user, progress) -> {
        EnderPearlEntity pearl = new EnderPearlEntity(world, user, Items.ENDER_PEARL.getDefaultStack());
        pearl.setPosition(user.getEyePos());
        pearl.setVelocity(user.getRotationVector().multiply(getHoldAmount(progress) * 1.5f + 0.5f));
        world.spawnEntity(pearl);
        return true;
    }), new Settings().maxDamage(8));

    public static float getHoldAmount(int progress) {
        return Math.min(1, -progress / 20f);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        return this.stopUsingFunction.stopUsing(stack, world, user, remainingUseTicks);
    }
}
