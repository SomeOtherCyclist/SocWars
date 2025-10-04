package com.soc.items.util;

import com.soc.SocWars;
import com.soc.items.*;
import com.soc.util.Sounds;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static final RegistryKey<ItemGroup> SOCWARS_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(SocWars.MOD_ID, "item_group"));
    public static final ItemGroup SOCWARS_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(AttackFunctionWeapon.DEVASTATOR_PRIME))
            .displayName(Text.translatable("itemGroup.socwars"))
            .build();

    public static void initialise() {
        Registry.register(Registries.ITEM_GROUP, SOCWARS_ITEM_GROUP_KEY, SOCWARS_ITEM_GROUP);

        Sounds.initialise();

        BaseWeapon.initialise();
        AttackFunctionWeapon.initialise();
        UseFunctionWeapon.initialise();
        PotionWeapon.initialise();
        DrawableWeapon.initialise();
        GreenSword.initialise();
        InvisibilityRing.initialise();
        PotionRing.initialise();
        DiceOfFate.initialise();
        PotionFood.initialise();
        EatFunctionFood.initialise();
        SharpenedPokingStick.initialise();
        GamblerArmour.initialise();
        GamblerSword.initialise();
        SteadfastArmour.initialise();
        ThrowableItem.initialise();
        ExtendoBridge.initialise();
    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SocWars.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static void addItemToGroups(Item item, RegistryKey<ItemGroup> _itemGroup) {
        ItemGroupEvents.modifyEntriesEvent(_itemGroup).register((itemGroup) -> itemGroup.add(item));
        ItemGroupEvents.modifyEntriesEvent(SOCWARS_ITEM_GROUP_KEY).register(itemGroup -> itemGroup.add(item));
    }

    public static void addItemToGroups(Item item) {
        ItemGroupEvents.modifyEntriesEvent(SOCWARS_ITEM_GROUP_KEY).register(itemGroup -> itemGroup.add(item));
    }
}