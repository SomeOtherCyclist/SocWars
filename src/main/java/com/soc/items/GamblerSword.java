package com.soc.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.soc.SocWars;
import com.soc.items.util.ModItems;
import com.soc.util.Random;
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
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class GamblerSword extends Item {
    private final int[] damageValues;
    private int damageValue;
    private int ticksUntilChange = 0;

    public static final Identifier ATTRIBUTE_ID = Identifier.of("gambler_damage");

    public GamblerSword(final Settings settings, final int[] protectionValues) {
        super(settings);
        this.damageValues = protectionValues;
        this.damageValue = protectionValues[2];
    }

    public static void initialise() {
        ModItems.addItemToGroups(GAMBLER_SWORD, ItemGroups.COMBAT);
    }

    public static final Item GAMBLER_SWORD = ModItems.register("gambler_sword", (settings) -> new GamblerSword(settings, new int[]{2, 4, 6, 8, 10}), new Settings().maxDamage(500).rarity(Rarity.RARE));

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        this.randomiseDamage();

        if (entity instanceof PlayerEntity playerEntity) {
            Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = ImmutableMultimap.of(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(ATTRIBUTE_ID, this.damageValue - 1, EntityAttributeModifier.Operation.ADD_VALUE));
            playerEntity.getAttributes().removeModifiers(map);

            if (slot != null && slot.getIndex() == 0) {
                playerEntity.getAttributes().addTemporaryModifiers(map);
            }
        }
    }

    private void randomiseDamage() {
        if (this.ticksUntilChange == 0) {
            this.damageValue = this.damageValues[Random.RANDOM.nextBetween(0, this.damageValues.length - 1)];
            this.ticksUntilChange = Random.RANDOM.nextBetween(4, 12);
        } else {
            this.ticksUntilChange--;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        Formatting formatting = switch(this.damageValue) {
            case 2 -> Formatting.RED;
            case 4 -> Formatting.GOLD;
            case 6 -> Formatting.YELLOW;
            case 8 -> Formatting.GREEN;
            case 10 -> Formatting.DARK_GREEN;
            default -> Formatting.BLACK;
        };

        textConsumer.accept(Text.empty());
        textConsumer.accept(Text.translatable("item.modifiers.mainhand").formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("gambler_attack_damage", new Object[]{this.damageValue}).formatted(formatting));
        textConsumer.accept(Text.translatable("attack_speed", new Object[]{1.6f}).formatted(Formatting.DARK_GREEN));
    }
}
