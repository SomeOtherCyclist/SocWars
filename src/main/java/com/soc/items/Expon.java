package com.soc.items;

import com.soc.items.components.ExponComponent;
import com.soc.items.util.ModItems;
import com.soc.materials.ToolMaterials;
import com.soc.networking.s2c.AddVelocityPayload;
import com.soc.util.DamageTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.soc.items.components.ModComponents.EXPON_COMPONENT;
import static com.soc.lib.SocWarsLib.damageSource;

public class Expon extends Item {
    public static final long DAMAGE_RESET_TIME = 4 * 20;

    public Expon(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(EXPON, ItemGroups.COMBAT);
    }

    public static final Item EXPON = ModItems.register("expon", Expon::new, new Item.Settings()
            .sword(ToolMaterials.BASE, -0.5f, -1.6f)
            .maxDamage(1000)
            .component(EXPON_COMPONENT, ExponComponent.DEFAULT)
            .rarity(Rarity.UNCOMMON)
    );

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        final ExponComponent component = stack.get(EXPON_COMPONENT);
        if (component == null) return;

        final long lastTimeUsed = component.lastTimeUsed();
        if (lastTimeUsed == 0) return;

        final long currentTime = world.getTime();
        final boolean shouldReset = currentTime - lastTimeUsed > DAMAGE_RESET_TIME;
        if (shouldReset) stack.set(EXPON_COMPONENT, ExponComponent.DEFAULT);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        final ExponComponent component = stack.get(EXPON_COMPONENT);
        if (component == null) return;

        final ServerWorld world = (ServerWorld)target.getWorld();
        final float damage = (1 << component.damageStage());
        if (attacker instanceof PlayerEntity playerEntity) {
            playerEntity.sendMessage(Text.of(String.valueOf(damage)), true); //maybe make a hud in 1.1?
        }

        target.damage(world, damageSource(world, DamageTypes.EXPON, attacker), damage);
        stack.set(EXPON_COMPONENT, component.doubleAndRefresh(world.getTime()));
        stack.damage(component.damageStage() - 1, attacker, Hand.MAIN_HAND);

        final Vec3d knockback = attacker.getRotationVector().multiply(0.2d).add(0d, 0.15d, 0d);
        if (target instanceof ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, new AddVelocityPayload(knockback));
        } else {
            target.addVelocity(knockback);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("tooltip.expon")); //Also maybe finish tooltip in 1.1
    }
}
