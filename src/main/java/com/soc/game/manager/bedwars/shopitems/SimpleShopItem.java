package com.soc.game.manager.bedwars.shopitems;

import com.google.gson.JsonObject;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.Optional;
import java.util.OptionalInt;

import static com.soc.lib.SocWarsLib.armourTrimFromColour;
import static com.soc.lib.json.JsonHelper.*;
import static net.minecraft.util.JsonHelper.deserialize;

public class SimpleShopItem implements ShopItem<SimpleShopItem>, TooltipProvider {
    public static final int ID = 1;
    private static final PacketCodec<RegistryByteBuf, SimpleShopItem> PACKET_CODEC = PacketCodec.tuple(Cost.PACKET_CODEC, SimpleShopItem::getCost, PacketCodecs.optional(ItemStack.PACKET_CODEC), SimpleShopItem::getOptionalStack, SimpleShopItem::new);

    public static final SimpleShopItem EMPTY = new SimpleShopItem(Cost.DEFAULT, ItemStack.EMPTY);

    private final Cost cost;
    private final ItemStack stack;

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public SimpleShopItem(Cost cost, Optional<ItemStack> stack) {
        this.cost = cost;
        this.stack = stack.orElse(ItemStack.EMPTY);
    }

    public SimpleShopItem(Cost cost, ItemStack stack) {
        this.cost = cost;
        this.stack = stack;
    }

    public SimpleShopItem(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDefaultedItem(object)
        );
    }

    public SimpleShopItem(Reader reader) {
        this(
                deserialize(reader)
        );
    }

    @Override
    public boolean buy(PlayerEntity player, AbstractShopScreenHandler context) {
        final boolean gaveStack = this.giveStack(this.stack, player, OptionalInt.empty());
        if (gaveStack) this.takeItems(player, context);

        return gaveStack;
    }

    private Optional<ItemStack> getOptionalStack() {
        return this.stack.isEmpty() ? Optional.empty() : Optional.of(this.stack);
    }

    @Override
    public ItemStack getIcon() {
        return this.stack;
    }

    @Override
    public Cost getCost() {
        return this.cost;
    }

    @Override
    public PacketCodec<RegistryByteBuf, SimpleShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public SimpleShopItem lazyClone() {
        return new SimpleShopItem(this.cost, this.stack.copy());
    }

    public void trim(DyeColor team, World world) {
        if (this.stack.isIn(ItemTags.TRIMMABLE_ARMOR)) {
            final Registry<ArmorTrimMaterial> materialRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.TRIM_MATERIAL);
            final Registry<ArmorTrimPattern> patternRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.TRIM_PATTERN);
            this.stack.set(DataComponentTypes.TRIM, new ArmorTrim(materialRegistry.getOrThrow(armourTrimFromColour(team)), patternRegistry.getOrThrow(ArmorTrimPatterns.FLOW)));
            this.stack.set(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE);
        }
    }

    @Override
    public void enchant(RegistryEntry<Enchantment> enchantment, int tier) {
        if (enchantment.value().isAcceptableItem(this.getIcon())) {
            this.getIcon().addEnchantment(enchantment, tier);
        }
    }

    @Override
    @Nullable
    public Text getTooltip() {
        return TooltipProvider.getEnchantmentTooltip(this.getIcon().get(DataComponentTypes.ENCHANTMENTS));
    }
}
