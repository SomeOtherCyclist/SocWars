package com.soc.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import com.soc.util.Random;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GamblerArmour extends Item {
    private final int[] protectionValues;
    private final EquipmentSlot slot;
    private int protectionValue;
    private int ticksUntilChange = 0;

    private static final RegistryKey<EquipmentAsset> GAMBLER_MODEL_KEY = ArmourItem.register("gambler");

    public GamblerArmour(final Settings settings, final int[] protectionValues, final EquipmentSlot slot) {
        super(settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(slot).equipSound(ArmorMaterials.DIAMOND.equipSound()).model(GAMBLER_MODEL_KEY).build()));
        this.slot = slot;
        this.protectionValues = protectionValues;
        this.protectionValue = protectionValues[2];
    }

    public static void initialise() {
        ModItems.addItemToGroups(GAMBLER_HELMET, ItemGroups.COMBAT);
        ModItems.addItemToGroups(GAMBLER_CHESTPLATE, ItemGroups.COMBAT);
        ModItems.addItemToGroups(GAMBLER_LEGGINGS, ItemGroups.COMBAT);
        ModItems.addItemToGroups(GAMBLER_BOOTS, ItemGroups.COMBAT);
    }

    public static final Item GAMBLER_HELMET = ModItems.register("gambler_helmet", (settings) -> new GamblerArmour(settings, new int[]{7, 5, 3, 1, 0}, EquipmentSlot.HEAD), new Settings().maxDamage(325).rarity(Rarity.RARE));
    public static final Item GAMBLER_CHESTPLATE = ModItems.register("gambler_chestplate", (settings) -> new GamblerArmour(settings, new int[]{9 ,7, 5, 3, 1}, EquipmentSlot.CHEST), new Settings().maxDamage(400).rarity(Rarity.RARE));
    public static final Item GAMBLER_LEGGINGS = ModItems.register("gambler_leggings", (settings) -> new GamblerArmour(settings, new int[]{8, 6, 4, 2, 0}, EquipmentSlot.LEGS), new Settings().maxDamage(375).rarity(Rarity.RARE));
    public static final Item GAMBLER_BOOTS = ModItems.register("gambler_boots", (settings) -> new GamblerArmour(settings, new int[]{5, 4, 2, 1, 0}, EquipmentSlot.FEET), new Settings().maxDamage(325).rarity(Rarity.RARE));

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        String slotName = "gambler." + this.slot.getName();
        this.randomiseArmour();

        if (entity instanceof PlayerEntity playerEntity) {
            Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = ImmutableMultimap.of(EntityAttributes.ARMOR, new EntityAttributeModifier(Identifier.ofVanilla(slotName), this.protectionValue, EntityAttributeModifier.Operation.ADD_VALUE));
            playerEntity.getAttributes().removeModifiers(map);

            if (slot != null && slot.isArmorSlot()) {
                playerEntity.getAttributes().addTemporaryModifiers(map);
            }
        }
    }

    private void randomiseArmour() {
        if (this.ticksUntilChange == 0) {
            this.protectionValue = this.protectionValues[Random.RANDOM.nextBetween(0, this.protectionValues.length - 1)];
            this.ticksUntilChange = Random.RANDOM.nextBetween(4, 12);
        } else {
            this.ticksUntilChange--;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        Formatting formatting = switch(this.protectionValue) {
            case 0 -> Formatting.DARK_RED;
            case 1 -> Formatting.RED;
            case 2,3 -> Formatting.GOLD;
            case 4,5 -> Formatting.YELLOW;
            case 6,7 -> Formatting.GREEN;
            case 8,9 -> Formatting.DARK_GREEN;
            default -> Formatting.BLACK;
        };

        textConsumer.accept(Text.empty());
        textConsumer.accept(Text.translatable("item.modifiers." + this.slot.getName()).formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("gambler_armour_amount", new Object[]{this.protectionValue}).formatted(formatting));
    }
}
