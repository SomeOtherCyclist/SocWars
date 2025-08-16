package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.items.util.RingItem;
import com.soc.players.PlayerData;
import com.soc.players.PlayerDataManager;
//import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import static com.soc.items.util.ModItems.addItemToGroups;

public class InvisibilityRing extends RingItem {

    public InvisibilityRing(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        addItemToGroups(INVISIBILITY_RING, ItemGroups.TOOLS);

        /*
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if (!itemStack.isOf(INVISIBILITY_RING)) {
                return;
            }
            list.add(Text.translatable("item.socwars.invisibility_ring.tooltip"));
        });
        */
    }

    public static final Item INVISIBILITY_RING = ModItems.register("invisibility_ring", InvisibilityRing::new, new Settings().maxDamage(20 * 40).rarity(Rarity.UNCOMMON));

    protected void ringUse(World world, PlayerEntity user, Hand hand) {
        PlayerData playerData = PlayerDataManager.getPlayerData(user.getUuid());

        playerData.invisible = true;
        user.setInvisible(true);
    }

    protected void ringFinishUse(World world, PlayerEntity user, Hand hand) {
        PlayerData playerData = PlayerDataManager.getPlayerData(user.getUuid());

        playerData.invisible = false;
        user.setInvisible(false);
    }
}
