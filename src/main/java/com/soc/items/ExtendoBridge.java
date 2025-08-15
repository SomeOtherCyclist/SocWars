package com.soc.items;

import com.soc.items.util.ModItems;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ExtendoBridge extends Item {
    public ExtendoBridge(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        ModItems.addItemToGroups(EXTENDO_BRIDGE);
    }

    public static final Item EXTENDO_BRIDGE = ModItems.register("extendo_bridge", ExtendoBridge::new, new Settings());

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("tooltip.extendo_bridge"));
    }
}
