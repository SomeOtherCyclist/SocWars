package com.soc.items;

import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.StatArmourBonus;
import com.soc.player.PlayerDataManager;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
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
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SteadfastArmour extends ArmourItem {

    private static final RegistryKey<EquipmentAsset> STEADFAST_MODEL_KEY = ArmourItem.register("steadfast");

    public SteadfastArmour(final Settings settings, final EquipmentSlot slot, final int armour) {
        super(settings, slot, armour, STEADFAST_MODEL_KEY);
    }

    public static void initialise() {
        ModItems.addItemToGroups(STEADFAST_HELMET, ItemGroups.COMBAT);
        ModItems.addItemToGroups(STEADFAST_CHESTPLATE, ItemGroups.COMBAT);
        ModItems.addItemToGroups(STEADFAST_LEGGINGS, ItemGroups.COMBAT);
        ModItems.addItemToGroups(STEADFAST_BOOTS, ItemGroups.COMBAT);
    }

    public static final Item STEADFAST_HELMET = ModItems.register("steadfast_helmet", (settings) -> new SteadfastArmour(settings, EquipmentSlot.HEAD, 2), new Settings().maxDamage(325).rarity(Rarity.RARE));
    public static final Item STEADFAST_CHESTPLATE = ModItems.register("steadfast_chestplate", (settings) -> new SteadfastArmour(settings, EquipmentSlot.CHEST, 6), new Settings().maxDamage(400).rarity(Rarity.RARE));
    public static final Item STEADFAST_LEGGINGS = ModItems.register("steadfast_leggings", (settings) -> new SteadfastArmour(settings, EquipmentSlot.LEGS, 5), new Settings().maxDamage(375).rarity(Rarity.RARE));
    public static final Item STEADFAST_BOOTS = ModItems.register("steadfast_boots", (settings) -> new SteadfastArmour(settings, EquipmentSlot.FEET, 2), new Settings().maxDamage(325).rarity(Rarity.RARE));

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof PlayerEntity playerEntity) {
            StatArmourBonus armourBonus = PlayerDataManager.getPlayerData(playerEntity.getUuid()).steadfastBonus;
            armourBonus.setPiece(this.slot, false);

            if (slot != null && slot.isArmorSlot()) {
                armourBonus.setPiece(this.slot, true);
            }

            armourBonus.setBonus(playerEntity);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.empty());
        textConsumer.accept(Text.translatable("item.modifiers." + this.slot.getName()).formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("attribute.modifier.plus.0", armour, "Armour").formatted(Formatting.BLUE));
        textConsumer.accept(Text.translatable("steadfast_knockback_amount", 20).formatted(Formatting.BLUE));
        textConsumer.accept(Text.translatable("full_set_worn").formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("steadfast_knockback_amount", 100).formatted(Formatting.DARK_GREEN));
    }
}
