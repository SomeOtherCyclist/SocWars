package com.soc.items.util;

import com.soc.items.components.ModComponents;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.util.function.Consumer;

public class DoubleJumpItem extends Item implements DisableOffhandSwapping {
	public DoubleJumpItem(Settings settings) {
		super(settings.component(ModComponents.DOUBLE_JUMP, true));
	}

	public static void initialise() {
		ItemGroups.addItemToItemsGroup(PORTABLE_STEPPING_STOOL);
	}

	public static final Item PORTABLE_STEPPING_STOOL = ModItems.register("portable_stepping_stool", DoubleJumpItem::new, new Settings().rarity(Rarity.RARE).maxCount(1));

	@Override
	@SuppressWarnings("deprecation")
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		textConsumer.accept(Text.translatable("tooltip.portable_stepping_stool"));
	}

	@Override
	public boolean shouldDisable(ItemStack stack) {
		return Boolean.FALSE.equals(stack.get(ModComponents.DOUBLE_JUMP));
	}
}
