package com.soc.items;

import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.OnHitArmour;
import com.soc.items.util.TransparentArmour;
import com.soc.player.PlayerDataManager;
import com.soc.util.DamageTypes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.Objects;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static com.soc.lib.SocWarsLib.damageSource;

public class IllusionerArmour extends ArmourItem implements OnHitArmour, TransparentArmour {
    private static final RegistryKey<EquipmentAsset> ILLUSIONER_MODEL_KEY = ArmourItem.registerEquipmentAsset("illusioner");

    public IllusionerArmour(Settings settings, EquipmentSlot slot, int armour) {
        super(settings, slot, armour, ILLUSIONER_MODEL_KEY);
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(ILLUSIONER_HELMET, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(ILLUSIONER_CHESTPLATE, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(ILLUSIONER_LEGGINGS, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(ILLUSIONER_BOOTS, ItemGroups.COMBAT);
    }

    public static final Item ILLUSIONER_HELMET = ModItems.register("illusioner_helmet", settings -> new IllusionerArmour(settings, EquipmentSlot.HEAD, 4), new Settings().maxDamage(325).rarity(Rarity.RARE));
    public static final Item ILLUSIONER_CHESTPLATE = ModItems.register("illusioner_chestplate", settings -> new IllusionerArmour(settings, EquipmentSlot.CHEST, 6), new Settings().maxDamage(400).rarity(Rarity.RARE));
    public static final Item ILLUSIONER_LEGGINGS = ModItems.register("illusioner_leggings", settings -> new IllusionerArmour(settings, EquipmentSlot.LEGS, 7), new Settings().maxDamage(375).rarity(Rarity.RARE));
    public static final Item ILLUSIONER_BOOTS = ModItems.register("illusioner_boots", settings -> new IllusionerArmour(settings, EquipmentSlot.FEET, 5), new Settings().maxDamage(325).rarity(Rarity.RARE));

    @Override
    public boolean onHit(ItemStack stack, LivingEntity wearer, World world, DamageSource source) {
        if (wearer instanceof ServerPlayerEntity serverPlayer) {
            final EquipmentSlot slot = Objects.requireNonNull(stack.get(DataComponentTypes.EQUIPPABLE)).slot();

            PlayerDataManager.getPlayerData(serverPlayer).addIllusion(world, slot);
        }

        return true;
    }
}
