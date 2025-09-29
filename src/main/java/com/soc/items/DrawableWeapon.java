package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.items.util.StopUsingFunction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.Optional;
import java.util.function.Function;

public class DrawableWeapon extends Item {
    private final StopUsingFunction stopUsingFunction;

    public DrawableWeapon(Settings settings, StopUsingFunction stopUsingFunction) {
        super(settings);
        this.stopUsingFunction = stopUsingFunction;
    }

    public static void initialise() {
        ModItems.addItemToGroups(ENDER_STAFF, ItemGroups.COMBAT);
        ModItems.addItemToGroups(WIND_STAFF, ItemGroups.COMBAT);
    }

    public static final Item ENDER_STAFF = ModItems.register("ender_staff", settings -> new DrawableWeapon(settings, (stack, world, user, progress) -> {
        EnderPearlEntity pearl = new EnderPearlEntity(world, user, Items.ENDER_PEARL.getDefaultStack());
        pearl.setPosition(user.getEyePos());
        pearl.setVelocity(user.getRotationVector().multiply(getHoldAmount(progress) * 1.5f + 0.5f));

        world.spawnEntity(pearl);

        stack.damage(1, user, user.getActiveHand());
        return false;
    }), new Settings().maxDamage(8).rarity(Rarity.UNCOMMON).useCooldown(0.5f));
    public static final Item WIND_STAFF = ModItems.register("wind_staff", settings -> new DrawableWeapon(settings, (stack, world, user, progress) -> {
        WindChargeEntity charge = new WindChargeEntity(EntityType.WIND_CHARGE, world) {
            @Override
            protected void createExplosion(Vec3d pos) {
                this.getWorld().createExplosion(
                        this,
                        null,
                        new AdvancedExplosionBehavior(true, false, Optional.of(1.2f + getHoldAmount(progress) * 0.3f), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())),
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        1.2F,
                        false,
                        World.ExplosionSourceType.TRIGGER,
                        ParticleTypes.GUST_EMITTER_SMALL,
                        ParticleTypes.GUST_EMITTER_LARGE,
                        SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
                );
            }
        };
        charge.setPosition(user.getEyePos());
        charge.setVelocity(user.getRotationVector().multiply(getHoldAmount(progress) * 1.5f + 0.5f));
        charge.setOwner(user);

        world.spawnEntity(charge);

        stack.damage(1, user, user.getActiveHand());
        return false;
    }), new Settings().maxDamage(12).rarity(Rarity.UNCOMMON).useCooldown(0.5f));

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
