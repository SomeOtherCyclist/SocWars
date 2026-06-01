package com.soc.game.manager.bedwars.shopitems;

import com.google.gson.JsonObject;
import com.soc.game.manager.BedwarsGameManager;
import com.soc.game.manager.bedwars.traps.AbstractTrap;
import com.soc.game.manager.bedwars.traps.Traps;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;

import static com.soc.lib.json.JsonHelper.*;
import static net.minecraft.util.JsonHelper.deserialize;

public class TrapShopItem implements ShopItem<TrapShopItem>, TooltipProvider {
    public static final int ID = 5;
    private static final PacketCodec<RegistryByteBuf, TrapShopItem> PACKET_CODEC = PacketCodec.tuple(Cost.PACKET_CODEC, TrapShopItem::getCost, Identifier.PACKET_CODEC, TrapShopItem::getTrapId, TrapShopItem::new);

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    private final Cost cost;
    private final AbstractTrap trap;

    public TrapShopItem(Cost cost, AbstractTrap trap) {
        this.cost = cost;
        this.trap = trap;
    }

    public TrapShopItem(Cost cost, Identifier id) {
        this.cost = cost;
        this.trap = Traps.getOrThrow(id);
    }

    public TrapShopItem(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDefaultedTrap(object, AbstractTrap.KEY)
        );
    }

    public TrapShopItem(Reader reader) {
        this(deserialize(reader));
    }

    @Override
    public boolean buy(PlayerEntity player, AbstractShopScreenHandler context) {
        if (!this.cost.canAfford(player)) return false;
        final BedwarsGameManager manager = context.getManager();

        final boolean queueHasSpace = player.getWorld().isClient ? context instanceof BedwarsTeamShopScreenHandler teamHandler && teamHandler.hasRoomInTraps() : manager.buyTrap((ServerPlayerEntity)player, this.trap);
        if (queueHasSpace) {
            this.takeItems(player, context);
            if (context instanceof BedwarsTeamShopScreenHandler teamShopScreenHandler) teamShopScreenHandler.onBuyTrap(this);
        }
        return queueHasSpace;
    }

    @Override
    public ItemStack getIcon() {
        return this.trap.getIcon();
    }

    @Override
    public Cost getCost() {
        return this.cost;
    }

    @Override
    public PacketCodec<RegistryByteBuf, TrapShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public TrapShopItem lazyClone() {
        return this;
    }

    private Identifier getTrapId() {
        return this.trap.getId();
    }

    @Override
    public Text getDisplayName() {
        return this.trap.getName();
    }

    public DisplayShopItem getDisplayCopy() {
        return this.trap.getDisplayShopItem();
    }

    @Override
    public @Nullable Text getTooltip() {
        return this.trap.getTooltip();
    }
}
