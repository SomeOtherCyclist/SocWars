package com.soc.items;

import com.soc.events.ModEvents;
import com.soc.items.util.CancelsBlockInteraction;
import com.soc.items.util.ItemGroups;
import com.soc.items.util.ModItems;
import com.soc.player.PlayerDataManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class MorphWand extends Item implements CancelsBlockInteraction {
	public static void initialise() {
		addItemToGroupsAndBaseItemGroup(MORPH_WAND, ItemGroups.ITEMS_KEY);
	}

	public MorphWand(Settings settings) {
		super(settings);
	}

	public static final Item MORPH_WAND = ModItems.register("morph_wand", MorphWand::new, new Settings()
			.rarity(Rarity.UNCOMMON)
	);

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
			if (serverPlayer.isSneaking()) {
				clearMorph(serverPlayer.getWorld(), serverPlayer);
			} else {
				final BlockState morph = context.getWorld().getBlockState(context.getBlockPos());

				final boolean allowMorph = ModEvents.ON_PLAYER_MORPHED.invoker().onPlayerMorphed(serverPlayer, morph);
				if (allowMorph) {
					PlayerDataManager.getPlayerData(serverPlayer).setMorph(context.getWorld(), morph);
					return ActionResult.SUCCESS;
				} else {
					return ActionResult.FAIL;
				}
			}
		}
		return ActionResult.FAIL;
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (user.isSneaking()) {
			if (user instanceof ServerPlayerEntity serverPlayer) clearMorph(world, serverPlayer);
			return ActionResult.SUCCESS;
		} else {
			return super.use(world, user, hand);
		}
	}

	private static void clearMorph(World world, ServerPlayerEntity serverPlayer) {
		PlayerDataManager.getPlayerData(serverPlayer).setMorph(world, null);
	}

	@Override
	public boolean shouldCancelInteraction() {
		return true;
	}
}
