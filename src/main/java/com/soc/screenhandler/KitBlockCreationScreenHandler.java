package com.soc.screenhandler;

import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.game.Kit;
import com.soc.game.manager.GameType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.List;
import java.util.Map;

import static com.soc.lib.SocWarsLib.ifNotNull;

public class KitBlockCreationScreenHandler extends ScreenHandler {
    public static final int ITEM_SLOTS_WIDTH = 5;
    public static final int ITEM_SLOTS_HEIGHT = 2;

    private final Kit kit;
    private KitBlockEntity blockEntity;

    public KitBlockCreationScreenHandler(int syncId, PlayerInventory playerInventory, Kit kit, KitBlockEntity blockEntity) {
        super(ScreenHandlers.KIT_BLOCK_CREATION_SCREEN_HANDLER, syncId);
        this.kit = kit;
        this.blockEntity = blockEntity;

        for (int y = 0; y < ITEM_SLOTS_HEIGHT; y++) {
            for (int x = 0; x < ITEM_SLOTS_WIDTH; x++) {
                this.addSlot(new Slot(this.kit, x + ITEM_SLOTS_WIDTH * y, x * 18 + 112, y * 18 + 152) {
                    @Override
                    public void markDirty() {
                        super.markDirty();
                        //KitBlockCreationScreenHandler.this.markDirty(); //TODO: Do this
                        ifNotNull(KitBlockCreationScreenHandler.this.blockEntity, blockEntity -> blockEntity.setKit(KitBlockCreationScreenHandler.this.kit));
                    }
                });
            }
        }
        this.addPlayerSlots(playerInventory, 40, 202);
    }

    public KitBlockCreationScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new Kit(), null);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        //TODO: Reqrite all of this since this is a copy paste from the minecraft code and I don't understand how it works at all
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < Kit.ITEM_SLOTS) {
                if (!this.insertItem(itemStack2, Kit.ITEM_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, Kit.ITEM_SLOTS, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }

        return itemStack;

        //return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.isCreative();
    }

    public KitBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public void setBlockEntity(KitBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public List<GameType> getAllowedGameTypesList() {
        return this.blockEntity == null ? List.of() : this.blockEntity.getAllowedGameTypesList();
    }

    public Map<GameType, Boolean> getAllowedGameTypes() {
        return this.blockEntity == null ? Map.of() : this.blockEntity.getAllowedGameTypes();
    }

    public void setGameTypeAllowed(GameType gameType, Boolean isAllowed) {
        if (this.blockEntity != null) this.blockEntity.setGameTypeAllowed(gameType, isAllowed);
    }
}
