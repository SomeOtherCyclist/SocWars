package com.soc.game.manager.bedwars.shopitems;

import com.soc.items.TrainingWeights;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.soc.lib.SocWarsLib.enchantment;
import static com.soc.lib.SocWarsLib.inventoryCanAcceptStack;

public interface ShopItem<INHERITOR> {
    Map<Integer, Function<RegistryByteBuf, ?>> DECODER_MAP = new HashMap<>();
    String ICON_KEY = "icon";

    boolean buy(PlayerEntity player, AbstractShopScreenHandler context);

    ItemStack getIcon();

    default Optional<ItemStack> getOptionalIcon() {
        return this.getIcon().isEmpty() ? Optional.empty() : Optional.of(this.getIcon());
    }

    Cost getCost();

    PacketCodec<RegistryByteBuf, INHERITOR> getPacketCodec();

    default void takeItems(PlayerEntity player, AbstractShopScreenHandler context) {
        this.getCost().takeItems(player, false);
        context.onBuyItem(this.getCost());
    }

    int id();

    @MustBeInvokedByOverriders
    @SuppressWarnings("unchecked")
    default void writePacketData(RegistryByteBuf byteBuf) {
        VarInts.write(byteBuf, this.id());
        this.getPacketCodec().encode(byteBuf, (INHERITOR)this);
    }

    default Text getDisplayName() {
        return getTooltipNameOfItem(this.getIcon());
    }

    static Text getTooltipNameOfItem(ItemStack icon) {
        return Text.literal(icon.getCount() + "x ").formatted(Formatting.DARK_PURPLE).append(icon.getItemName().copy().formatted(icon.getRarity().getFormatting()));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default boolean giveStack(ItemStack stack, PlayerEntity player, OptionalInt slot) {
        return this.giveStackNoCopy(stack.copy(), player, slot);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default boolean giveStack(ItemStack stack, PlayerEntity player, OptionalInt slot, Consumer<ItemStack> stackModifier) {
        final ItemStack modifiedStack = stack.copy();
        stackModifier.accept(modifiedStack);
        return this.giveStackNoCopy(modifiedStack, player, slot);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private boolean giveStackNoCopy(ItemStack stack, PlayerEntity player, OptionalInt slot) {
        if (!this.getCost().canAfford(player)) return false;

        final EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);

        if (equippableComponent != null && equippableComponent.slot().isArmorSlot()) {
            if (ItemStack.areItemsEqual(player.getEquippedStack(equippableComponent.slot()), stack)) return false;

            if (!stack.isOf(TrainingWeights.TRAINING_WEIGHTS)) stack.addEnchantment(enchantment(player.getWorld(), Enchantments.BINDING_CURSE), 1);

            player.equipStack(equippableComponent.slot(), stack);
            return true;
        } else {
            if (slot.isPresent()) {
                player.getInventory().setStack(slot.getAsInt(), stack);
                return true;
            } else {
                if (inventoryCanAcceptStack(player.getInventory(), stack)) {
                    player.giveItemStack(stack);
                    return true;
                }
                return false;
            }
        }
    }

    default void enchant(RegistryEntry<Enchantment> enchantment, int tier) {}

    static void applyEnchantmentIfApplicable(ItemStack stack, RegistryEntry<Enchantment> enchantment, int tier) {
        if (enchantment.value().isAcceptableItem(stack)) {
            stack.addEnchantment(enchantment, tier);
        }
    }

    default Text affordabilitySuffix(PlayerEntity player) {
        return this.getCost().canAfford(player) ? Text.literal(" ✔").formatted(Formatting.GREEN) : Text.empty();
    }

    /// Inheritors with mutable data should return a deeper clone, anything immutable can simply return a reference to itself
    INHERITOR lazyClone();
}
