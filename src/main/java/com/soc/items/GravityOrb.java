package com.soc.items;

import com.soc.items.util.ModItems;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.soc.items.util.ModItems.addItemToGroups;

public class GravityOrb extends Item {
    public GravityOrb(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        addItemToGroups(GRAVITY_ORB, ItemGroups.TOOLS);
    }

    public static final Item GRAVITY_ORB = ModItems.register("gravity_orb", GravityOrb::new, new Settings()
            .useCooldown(5f)
            .rarity(Rarity.UNCOMMON)
    );

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.getStackInHand(hand).decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof PlayerEntity playerEntity) {
            float cooldownProgress = playerEntity.getItemCooldownManager().getCooldownProgress(stack, 0);

            double gravity = playerEntity.getAttributeValue(EntityAttributes.GRAVITY);
            if (cooldownProgress > 0 && gravity > 0) {
                playerEntity.getAttributeInstance(EntityAttributes.GRAVITY).addTemporaryModifier(new EntityAttributeModifier(Identifier.of("gravity_orb"), -2d * gravity, EntityAttributeModifier.Operation.ADD_VALUE));
            } else if (cooldownProgress < 10E-5 && gravity < 0) {
                playerEntity.getAttributeInstance(EntityAttributes.GRAVITY).removeModifier(Identifier.of("gravity_orb"));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("tooltip.gravity_orb"));
    }
}
