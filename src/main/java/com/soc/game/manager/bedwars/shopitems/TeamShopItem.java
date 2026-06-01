package com.soc.game.manager.bedwars.shopitems;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.soc.game.map.DyeColourWithEmpty;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

import java.io.Reader;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import static com.soc.lib.json.JsonHelper.*;
import static net.minecraft.util.JsonHelper.deserialize;

public class TeamShopItem implements ShopItem<TeamShopItem>, TeamItem {
    public static final int ID = 3;
    private static final PacketCodec<RegistryByteBuf, TeamShopItem> PACKET_CODEC = PacketCodec.tuple(Cost.PACKET_CODEC, TeamShopItem::getCost, PacketCodecs.optional(ItemStack.PACKET_CODEC), TeamShopItem::getOptionalStack, TeamShopItem::new);

    public static final String MAP_KEY = "map";
    private static final ItemStack DOUBLE_DEFAULT_STACK = Items.RESIN_BLOCK.getDefaultStack().copy();

    static {
        DOUBLE_DEFAULT_STACK.set(DataComponentTypes.ITEM_NAME, Text.of("ERROR_ITEM"));
    }

    private final Cost cost;
    private Either<ItemStack, Map<DyeColourWithEmpty, ItemStack>> stackMap;

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public TeamShopItem(Cost cost, Optional<ItemStack> stack) {
        this.cost = cost;
        this.stackMap = Either.left(stack.orElse(DOUBLE_DEFAULT_STACK));
    }

    public TeamShopItem(Cost cost, Map<DyeColourWithEmpty, ItemStack> stackMap) {
        this.cost = cost;
        this.stackMap = Either.right(stackMap);
    }

    public TeamShopItem(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDoubleDefaultedDyeColourItemStackMap(object, MAP_KEY, DOUBLE_DEFAULT_STACK)
        );
    }

    public TeamShopItem(Reader reader) {
        this(
                deserialize(reader)
        );
    }

    @Override
    public void setTeam(DyeColor team) {
        this.stackMap.right().ifPresent(map -> this.stackMap = Either.left(map.get(DyeColourWithEmpty.fromDyeColour(team))));
    }

    @Override
    public boolean buy(PlayerEntity player, AbstractShopScreenHandler context) {
        final boolean gaveStack = this.giveStack(this.getStack(), player, OptionalInt.empty());
        if (gaveStack) this.takeItems(player, context);

        return gaveStack;
    }

    private ItemStack getStack() {
        if (this.stackMap.left().isPresent()) return this.stackMap.left().get();
        if (this.stackMap.right().isPresent()) return this.stackMap.right().get().get(DyeColourWithEmpty.EMPTY);
        return DOUBLE_DEFAULT_STACK;
    }

    private Optional<ItemStack> getOptionalStack() {
        final ItemStack stack = this.getStack();
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack);
    }

    @Override
    public ItemStack getIcon() {
        return this.getStack();
    }

    @Override
    public Cost getCost() {
        return this.cost;
    }

    @Override
    public PacketCodec<RegistryByteBuf, TeamShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public void enchant(RegistryEntry<Enchantment> enchantment, int tier) {
        this.stackMap.ifLeft(stack -> ShopItem.applyEnchantmentIfApplicable(stack, enchantment, tier));
        this.stackMap.ifRight(map -> map.values().forEach(stack -> ShopItem.applyEnchantmentIfApplicable(stack, enchantment, tier)));
    }

    /// Should only be called before {@link TeamItem#setTeam} has been called
    @Override
    public TeamShopItem lazyClone() {
        return new TeamShopItem(this.cost, this.stackMap.right().get());
    }
}
