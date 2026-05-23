package com.soc.items;

import com.soc.events.ModEvents;
import com.soc.items.util.CancelsBlockInteraction;
import com.soc.items.util.ItemGroups;
import com.soc.items.util.ModItems;
import com.soc.items.util.OnAttackButtonPressed;
import com.soc.mixin.AccessDebugStickCycle;
import com.soc.player.PlayerData;
import com.soc.player.PlayerDataManager;
import com.soc.util.BlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class MorphWand extends Item implements CancelsBlockInteraction, OnAttackButtonPressed {
	private static final List<Property<?>> CYCLING_PROPERTIES = List.of(
			Properties.AXIS,
			Properties.FACING,
			Properties.HORIZONTAL_FACING,
			Properties.BLOCK_FACE,
			Properties.ROTATION,
			Properties.SLAB_TYPE
	);

	public static void initialise() {
		addItemToGroupsAndBaseItemGroup(MORPH_WAND, ItemGroups.ITEMS_KEY);
	}

	public MorphWand(Settings settings) {
		super(settings);
	}

	public static final Item MORPH_WAND = ModItems.register("morph_wand", MorphWand::new, new Settings());

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
			return use(context.getWorld(), serverPlayer, context.getHand(), context.getWorld().getBlockState(context.getBlockPos()));
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (user instanceof ServerPlayerEntity serverPlayer) {
			return use(world, serverPlayer, hand, null);
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public void onAttackButtonPressed(PlayerEntity player) {
		rotateMorph(player.getWorld(), player, player.isSneaking());
	}

	private static ActionResult use(World world, ServerPlayerEntity player, Hand hand, @Nullable BlockState blockState) {
		if (player.isSneaking()) {
			return clearMorph(world, player);
		} else if (blockState != null) {
			final ActionResult result = attemptMorph(world, player, blockState);
			if (result.isAccepted()) player.getItemCooldownManager().set(player.getStackInHand(hand), 15 * 20);
			return result;
		}
		return ActionResult.FAIL;
	}

	private static ActionResult attemptMorph(World world, ServerPlayerEntity player, BlockState blockState) {
		if (blockState.isIn(BlockTags.DISALLOW_MORPH)) {
			player.sendMessage(Text.literal("Nice try ;)"));
			return ActionResult.FAIL;
		}

		final boolean allowMorph = ModEvents.ON_PLAYER_MORPHED.invoker().onPlayerMorphed(player, blockState);
		if (allowMorph) {
			PlayerDataManager.getPlayerData(player).setMorph(world, blockState);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}

	private static ActionResult clearMorph(World world, ServerPlayerEntity serverPlayer) {
		PlayerDataManager.getPlayerData(serverPlayer).setMorph(world, null);
		return ActionResult.SUCCESS;
	}

	private static void rotateMorph(World world, PlayerEntity player, boolean reverse) {
		final PlayerData playerData = PlayerDataManager.getSideLocalPlayerData(player);
		final BlockState morph = playerData.getMorph();

		if (morph != null) CYCLING_PROPERTIES.stream().filter(morph::contains).findFirst().ifPresent(property -> {
			playerData.setMorph(world, AccessDebugStickCycle.cycle(morph, property, reverse));
			player.getItemCooldownManager().set(player.getStackInHand(Hand.OFF_HAND), 3 * 20);
		});
	}

	@Override
	public boolean shouldCancelInteraction() {
		return true;
	}
}
