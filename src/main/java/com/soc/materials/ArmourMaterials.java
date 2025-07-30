package com.soc.materials;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.sound.SoundEvents;

import java.util.Map;

public class ArmourMaterials {
    public static void initialise() {

    }

    public static ArmorMaterial GAMBLER_ARMOUR_MATERIAL = new ArmorMaterial(
            25,
            Map.of(
                    EquipmentType.HELMET, 3,
                    EquipmentType.CHESTPLATE, 8,
                    EquipmentType.LEGGINGS, 6,
                    EquipmentType.BOOTS, 3
            ),
            5,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            0.0F,
            0.0F,
            null,
            null
    );

    public static void randomizeGamblerProtection() {
        GAMBLER_ARMOUR_MATERIAL = new ArmorMaterial(
                25,
                Map.of(
                        EquipmentType.HELMET, 3,
                        EquipmentType.CHESTPLATE, 8,
                        EquipmentType.LEGGINGS, 6,
                        EquipmentType.BOOTS, 3
                ),
                5,
                SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                0.0F,
                0.0F,
                null,
                null
        );
    }

    //net.minecraft.block.TntBlock
    //net.minecraft.entity.Entity
}