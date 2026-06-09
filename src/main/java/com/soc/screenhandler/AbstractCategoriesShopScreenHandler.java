package com.soc.screenhandler;

import com.soc.game.manager.bedwars.BedwarsShopCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCategoriesShopScreenHandler extends AbstractShopScreenHandler {
    protected final Inventory categories;

    protected BedwarsShopCategory currentCategory;

    protected AbstractCategoriesShopScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, syncId, playerInventory, player);
        this.categories = this.createCategoriesInventory();
    }

    protected abstract Inventory createCategoriesInventory();

    public void setCurrentCategory(int slot) {
        if (this.shopContents == null || slot < 0 || slot >= this.shopContents.getNumCategories()) return;
        this.currentCategory = this.shopContents.getCategory(slot);

        this.refreshItems();
    }

    protected void refreshCategories() {
        for (int i = 0; i < this.categories.size(); i++) {
            this.categories.setStack(i, this.getCategoryIcon(i));
        }
    }

    public ItemStack getCategoryIcon(int slot) {
        if (this.shopContents == null || slot < 0 || slot >= this.shopContents.getNumCategories()) return ItemStack.EMPTY;
        return this.shopContents.getCategoryIcons().get(slot);
    }

    public BedwarsShopCategory getShopCategory(Slot slot) {
        return this.getShopCategory(slot.getIndex());
    }

    public BedwarsShopCategory getShopCategory(int slot) {
        return this.shopContents.getCategory(slot);
    }

    public BedwarsShopCategory getCurrentCategory() {
        return this.currentCategory;
    }

    public Text getCurrentCategoryName() {
        return this.currentCategory != null ? this.currentCategory.getName() : Text.empty();
    }
}
