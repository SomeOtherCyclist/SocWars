package com.soc.game.manager.bedwars.shopitems;

import com.google.gson.JsonObject;
import com.soc.game.manager.BedwarsGameManager;
import com.soc.game.manager.bedwars.traps.Abilities;
import com.soc.game.manager.bedwars.traps.AbstractAbility;
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

public class AbilityShopItem implements ShopItem<AbilityShopItem>, TooltipProvider {
    public static final int ID = 8;
    private static final PacketCodec<RegistryByteBuf, AbilityShopItem> PACKET_CODEC = PacketCodec.tuple(Cost.PACKET_CODEC, AbilityShopItem::getCost, Identifier.PACKET_CODEC, AbilityShopItem::getAbilityId, AbilityShopItem::new);

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    private final Cost cost;
    private final AbstractAbility ability;

    public AbilityShopItem(Cost cost, AbstractAbility ability) {
        this.cost = cost;
        this.ability = ability;
    }

    public AbilityShopItem(Cost cost, Identifier id) {
        this.cost = cost;
        this.ability = Abilities.getOrThrow(id);
    }

    public AbilityShopItem(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDefaultedAbility(object, AbstractAbility.KEY)
        );
    }

    public AbilityShopItem(Reader reader) {
        this(deserialize(reader));
    }

    @Override
    public boolean buy(PlayerEntity player, AbstractShopScreenHandler context) {
        if (!this.cost.canAfford(player)) return false;
        final BedwarsGameManager manager = context.getManager();

        final boolean queueHasSpace = player.getWorld().isClient ? context instanceof BedwarsTeamShopScreenHandler teamHandler && teamHandler.hasRoomInAbilities() : manager.buyAbility((ServerPlayerEntity)player, this.ability);
        if (queueHasSpace) {
            this.takeItems(player, context);
            if (context instanceof BedwarsTeamShopScreenHandler teamShopScreenHandler) teamShopScreenHandler.onBuyAbility(this);
        }
        return queueHasSpace;
    }

    @Override
    public ItemStack getIcon() {
        return this.ability.getIcon();
    }

    @Override
    public Cost getCost() {
        return this.cost;
    }

    @Override
    public PacketCodec<RegistryByteBuf, AbilityShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public AbilityShopItem lazyClone() {
        return this;
    }

    private Identifier getAbilityId() {
        return this.ability.getId();
    }

    @Override
    public Text getDisplayName() {
        return this.ability.getName();
    }

    public DisplayShopItem getDisplayCopy() {
        return this.ability.getDisplayShopItem();
    }

    @Override
    public @Nullable Text getTooltip() {
        return this.ability.getTooltip();
    }
}
