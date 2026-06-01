package com.soc.game.manager.bedwars.shopitems;

import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public abstract class TieredShopItem<INHERITOR> implements ShopItem<INHERITOR> {
    public static final String COSTS_KEY = "costs";

    protected int tier;
    protected final List<Cost> costs;

    protected TieredShopItem(List<Cost> costs, int tier) {
        this.tier = tier;
        this.costs = costs;
    }

    @Override
    public Cost getCost() {
        return this.tier < this.costs.size() ? this.costs.get(this.tier) : Cost.DEFAULT;
    }

    @Override
    public Text affordabilitySuffix(PlayerEntity player) {
        return this.tier < this.costs.size() ? ShopItem.super.affordabilitySuffix(player) : Text.translatable("game.bedwars.shop.item.max_tier").formatted(Formatting.YELLOW, Formatting.BOLD);
    }

    protected List<Cost> getCosts() {
        return this.costs;
    }

    @Override
    public boolean buy(PlayerEntity player, AbstractShopScreenHandler context) {
        if (this.tier == this.costs.size() || !this.getCost().canAfford(player)) return false;

        this.takeItems(player, context);

        this.tier++;
        this.getIcon().setCount(Math.min(this.tier + 1, this.costs.size()));

        context.refreshItems();

        return true;
    }

    public int getTier() {
        return this.tier;
    }

    protected abstract MutableText getBaseName();

    @Override
    public Text getDisplayName() { //Yeah I know it still has the enchantment.level stuff but I have been too lazy to make a proper roman numerals thing
        final Text oldLevel = Text.translatable("enchantment.level." + this.tier).formatted(this.tier == this.costs.size() ? Formatting.BLUE : Formatting.GREEN);
        final Text newLevel = Text.translatable("enchantment.level." + (this.tier + 1)).formatted(Formatting.BLUE);
        final Text suffix = this.tier == this.costs.size() ? oldLevel : Text.translatable("hud.a_to_b", oldLevel, newLevel).formatted(Formatting.AQUA);
        return this.getBaseName().append(" ").append(suffix);
    }
}
