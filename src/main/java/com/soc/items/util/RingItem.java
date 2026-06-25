package com.soc.items.util;

import com.soc.SocWars;
import com.soc.items.components.ModComponents;
import com.soc.items.components.RingItemComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class RingItem extends Item {
    public static final int GRACE_TICKS = 9;

    public RingItem(Item.Settings settings) {
        super(settings
                .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                .component(ModComponents.RING_ITEM_COMPONENT, new RingItemComponent(0, false))
        );
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        final ItemStack stack = user.getStackInHand(hand);
        stack.set(ModComponents.RING_ITEM_COMPONENT, new RingItemComponent(world.getTime(), true));

        this.ringUse(user);

        return ActionResult.FAIL;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof LivingEntity user) {
            final RingItemComponent component = stack.get(ModComponents.RING_ITEM_COMPONENT);
            if (component == null) {
                SocWars.LOGGER.warn("Some bozo created options RingItem without its data component");
                return;
            }

            if (component.isUsing()) stack.damage(1, user, slot);

            if (component.lastTimeUsed() < world.getTime() - GRACE_TICKS || stack.shouldBreak()) {
                stack.set(ModComponents.RING_ITEM_COMPONENT, new RingItemComponent(0, false));
                this.ringFinishUse(user);
            }
        }
    }

    protected void ringUse(LivingEntity user) {}
    protected void ringFinishUse(LivingEntity user) {}
}
