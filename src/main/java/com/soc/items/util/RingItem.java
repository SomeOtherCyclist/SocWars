package com.soc.items.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class RingItem extends Item {
    private long lastTimeUsed = 0;
    private boolean isUsing = false;

    private World world;
    private PlayerEntity user;
    private Hand hand;

    public RingItem(Item.Settings settings) {
        super(settings.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        this.world = world;
        this.user = user;
        this.hand = hand;

        this.lastTimeUsed = world.getTime();
        this.isUsing = true;

        this.ringUse(world, user, hand);

        return ActionResult.FAIL;
    }

    protected void ringUse(World world, PlayerEntity user, Hand hand) {}

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);

        if (this.isUsing) {
            ItemStack item = this.user.getStackInHand(this.hand);
            item.setDamage(item.getDamage() + 1);

            if (item.shouldBreak()) {
                this.user.setStackInHand(this.hand, ItemStack.EMPTY);
                this.user.playSound(SoundEvent.of(Identifier.of("minecraft:entity.item.break")), 1, 1);

                this.isUsing = false;
                this.ringFinishUse(this.world, this.user, this.hand);
            }

            if (this.lastTimeUsed < world.getTime() - 5) {
                this.isUsing = false;
                this.ringFinishUse(this.world, this.user, this.hand);
            }
        }
    }

    protected void ringFinishUse(World world, PlayerEntity user, Hand hand) {}
}
