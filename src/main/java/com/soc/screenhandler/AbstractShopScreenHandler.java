package com.soc.screenhandler;

import com.soc.game.manager.BedwarsGameManager;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.resourcedata.deserialisation.Cost;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

import static com.soc.lib.SocWarsLib.ifNotNull;

public abstract class AbstractShopScreenHandler extends ScreenHandler {
    protected final PlayerEntity player;
    protected final PlayerInventory playerInventory;
    protected final BedwarsGameManager manager;

    protected BedwarsShopContents shopContents;

    protected AbstractShopScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, syncId);
        this.player = player;
        this.playerInventory = playerInventory;
        this.manager = BedwarsGameManager.getBedwarsGameManager(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.manager != null || player.getWorld().isClient ;
    }

    public final BedwarsGameManager getManager() {
        return this.manager;
    }

    public abstract ShopItem<?> getShopItem(int slot);

    public ShopItem<?> getShopItem(Slot slot) {
        return this.getShopItem(slot.id);
    }

    public abstract void refreshItems();

    public abstract int getPlayerInventorySlotHeight();

    public abstract void setShopContents(BedwarsShopContents shopContents);

    public void onBuyItem(Cost cost) {
        ifNotNull(this.manager, manager -> manager.onBuyItem(cost, this.player));
    }
}
