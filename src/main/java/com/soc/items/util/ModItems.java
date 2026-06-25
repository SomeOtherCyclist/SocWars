package com.soc.items.util;

import com.soc.SocWars;
import com.soc.effects.util.ModEffects;
import com.soc.items.*;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.function.Function;

import static com.soc.items.util.ItemGroups.addItemToItemsGroup;

public interface ModItems {
    static void initialise() {
        ItemGroups.initialise();

        HardCodes.initialise();

        BaseWeapon.initialise();
        AttackFunctionWeapon.initialise();
        UseFunctionWeapon.initialise();
        PotionWeapon.initialise();
        DrawableWeapon.initialise();
        BowItem.initialise();
        GreenSword.initialise();
        PotionRing.initialise();
        DiceOfFate.initialise();
        PotionFood.initialise();
        EatFunctionFood.initialise();
        GamblerArmour.initialise();
        GamblerSword.initialise();
        SteadfastArmour.initialise();
        CartoonArmour.initialise();
        GlassArmour.initialise();
        DemolitionistArmour.initialise();
        IllusionerArmour.initialise();
        TrainingWeights.initialise();
        SummonersGarb.initialise();
        Expon.initialise();
        ThrowableItem.initialise();
        ExtendoBridge.initialise();
        BlockItems.initialise();
        BaseTool.initialise();
        BaseItem.initialise();
        DoubleJumpItem.initialise();
        JetShoppingTrolley.initialise();
        MorphWand.initialise();
        CommandFunctionItem.initialise();
    }

    static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        final RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SocWars.MOD_ID, name));

        // Create the item instance.
        final Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }
}