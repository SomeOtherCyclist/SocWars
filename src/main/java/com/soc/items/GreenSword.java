package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.materials.ToolMaterials;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

public class GreenSword extends Item {
    public GreenSword(final Settings settings) {
        super(settings);
    }

    public static void initialise() {
        ModItems.addItemToGroups(GREEN_SWORD, ItemGroups.COMBAT);
    }

    public static final Item GREEN_SWORD = ModItems.register("green_sword", GreenSword::new, new Settings()
            .sword(ToolMaterials.BASE, 7f, -2.15f)
            .rarity(Rarity.UNCOMMON)
            .maxDamage(700)
    );

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND && entity instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 5, 1, false, false));
        }
    }
}
