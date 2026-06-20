package com.soc.items;

import com.soc.items.components.CommandFunctionComponent;
import com.soc.items.components.ModComponents;
import com.soc.items.util.ItemGroups;
import com.soc.items.util.ModItems;
import com.soc.items.util.OnAttackButtonPressed;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static com.soc.lib.SocWarsLib.ifNotNull;

public class CommandFunctionItem extends Item implements OnAttackButtonPressed {
	public CommandFunctionItem(Settings settings) {
		super(settings);
	}

	public static void initialise() {
		addItemToGroupsAndBaseItemGroup(PARKOUR_RESET, ItemGroups.ITEMS_KEY);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ifNotNull(user.getStackInHand(hand).get(ModComponents.COMMAND_FUNCTION), component -> component.runRightClick(user));

		return super.use(world, user, hand);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		ifNotNull(context.getStack().get(ModComponents.COMMAND_FUNCTION), component -> component.runRightClick(context.getPlayer()));

		return super.useOnBlock(context);
	}

	@Override
	public void onAttackButtonPressed(PlayerEntity player, ItemStack stack) {
		ifNotNull(stack.get(ModComponents.COMMAND_FUNCTION), component -> component.runLeftClick(player));
	}

	private static final Item PARKOUR_RESET = ModItems.register("parkour_reset", CommandFunctionItem::new, new Settings()
			.component(ModComponents.COMMAND_FUNCTION, new CommandFunctionComponent("parkour:cancel", "parkour:reset"))
	);
}
