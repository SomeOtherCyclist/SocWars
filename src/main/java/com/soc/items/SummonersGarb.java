package com.soc.items;

import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.OnHitArmour;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.function.Consumer;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static com.soc.lib.SocWarsLib.copyTeam;
import static com.soc.lib.SocWarsLib.randomHostileMob;

public class SummonersGarb extends ArmourItem implements OnHitArmour {
    public static final float HIT_SPAWN_CHANCE = 0.4f;

    private static final RegistryKey<EquipmentAsset> SUMMONERS_GARB_MODEL_KEY = ArmourItem.registerEquipmentAsset("summoners_garb");

    public SummonersGarb(Settings settings, EquipmentSlot slot, int armour) {
        super(settings, slot, armour, SUMMONERS_GARB_MODEL_KEY);
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(SUMMONERS_GARB, ItemGroups.COMBAT);
    }

    public static final Item SUMMONERS_GARB = ModItems.register("summoners_garb", settings -> new SummonersGarb(settings, EquipmentSlot.CHEST, 6), new Settings()
            .rarity(Rarity.RARE)
            .maxDamage(400)
    );

    @Override
    public boolean onHit(ItemStack stack, LivingEntity wearer, World world, DamageSource source) {
        if (!world.isClient && world instanceof ServerWorld serverWorld && world.random.nextFloat() > HIT_SPAWN_CHANCE) {
            final LivingEntity mob = randomHostileMob(serverWorld, wearer.getPos());
            copyTeam(world, mob, wearer);

            world.spawnEntity(mob);
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("summoners_garb.spawn_chance", (int)(HIT_SPAWN_CHANCE * 100f)).formatted(Formatting.GOLD));
    }
}
