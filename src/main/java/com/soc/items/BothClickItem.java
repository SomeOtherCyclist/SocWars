package com.soc.items;

import com.soc.items.components.CommandFunctionComponent;
import com.soc.items.components.ModComponents;
import com.soc.items.util.ItemGroups;
import com.soc.items.util.ModItems;
import com.soc.items.util.OnAttackButtonPressed;
import com.soc.items.util.UseFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static com.soc.lib.SocWarsLib.ifNotNull;
import static com.soc.lib.SocWarsLib.mapIfNotNull;

public class BothClickItem extends Item implements OnAttackButtonPressed {
	@Nullable private final UseFunction leftClickFunction;
	@Nullable private final UseFunction rightClickFunction;

	public BothClickItem(Settings settings, @Nullable UseFunction leftClickFunction, @Nullable UseFunction rightClickFunction) {
		super(settings);
		this.leftClickFunction = leftClickFunction;
		this.rightClickFunction = rightClickFunction;
	}

	public static BothClickItem leftClickItem(Settings settings, UseFunction leftClickFunction) {
		return new BothClickItem(settings, leftClickFunction, null);
	}

	public static BothClickItem rightClickItem(Settings settings, UseFunction rightClickFunction) {
		return new BothClickItem(settings, null, rightClickFunction);
	}

	public static void initialise() {
		addItemToGroupsAndBaseItemGroup(PARKOUR_RESET, ItemGroups.ITEMS_KEY);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		return mapIfNotNull(this.rightClickFunction, function -> function.use(world, user, hand), ActionResult.PASS);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		return mapIfNotNull(this.rightClickFunction, function -> function.use(context.getWorld(), context.getPlayer(), context.getHand()), ActionResult.PASS);
	}

	@Override
	public void onAttackButtonPressed(PlayerEntity player, ItemStack stack) {
		ifNotNull(this.leftClickFunction, function -> function.use(player.getWorld(), player, Hand.MAIN_HAND));
	}

	private static final Item PARKOUR_RESET = ModItems.register("parkour_reset", settings -> new BothClickItem(settings,
					(world, user, hand) -> {
						ifNotNull(user.getStackInHand(hand).get(ModComponents.COMMAND_FUNCTION), component -> component.runLeftClick(user));
						return ActionResult.SUCCESS;
					},
					(world, user, hand) -> {
						ifNotNull(user.getStackInHand(hand).get(ModComponents.COMMAND_FUNCTION), component -> component.runRightClick(user));
						return ActionResult.SUCCESS;
					}
			), new Settings()
			.component(ModComponents.COMMAND_FUNCTION, new CommandFunctionComponent("parkour:cancel", "parkour:checkpoint"))
	);
}
