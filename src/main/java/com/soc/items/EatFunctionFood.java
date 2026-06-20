package com.soc.items;

import com.soc.items.util.FinishUsingFunction;
import com.soc.items.util.ModItems;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.function.Consumer;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static com.soc.lib.SocWarsLib.*;

public class EatFunctionFood extends Item { //rewrite all of this as consumable components because this is just gross and dirty and I hate it
    public static final int CHORUS_SALAD_TRIES = 50;

    private final FinishUsingFunction finishUsingFunction;

    public EatFunctionFood(final Item.Settings settings, final FinishUsingFunction finishUsingFunction, FoodComponent foodComponent) {
        super(settings.food(foodComponent));
        this.finishUsingFunction = finishUsingFunction;
    }

    public EatFunctionFood(final Item.Settings settings, final FinishUsingFunction finishUsingFunction) {
        this(settings, finishUsingFunction, new FoodComponent(0, 1, true));
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(SHRINKING_PILLS, ItemGroups.FOOD_AND_DRINK);
        addItemToGroupsAndBaseItemGroup(BIGGENING_PILLS, ItemGroups.FOOD_AND_DRINK);
        addItemToGroupsAndBaseItemGroup(CHORUS_FRUIT_SALAD, ItemGroups.FOOD_AND_DRINK);
    }

    public static final Item SHRINKING_PILLS = ModItems.register("shrinking_pills", settings -> new EatFunctionFood(settings, (stack, world, user) -> {
        scaleEntity(user, SQRT2 * 0.5f);
        return null;
            }), new Settings()
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item BIGGENING_PILLS = ModItems.register("biggening_pills", settings -> new EatFunctionFood(settings, (stack, world, user) -> {
        scaleEntity(user, SQRT2);
        return null;
            }), new Settings()
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item CHORUS_FRUIT_SALAD = ModItems.register("chorus_fruit_salad", settings -> new EatFunctionFood(settings, (stack, world, user) -> {
        if (world.isClient) return null;

        final boolean success = randomTeleport(world, user, CHORUS_SALAD_TRIES, 50, 15f);
        if (!success && user instanceof PlayerEntity player) player.sendMessage(Text.translatable("chorus_fruit_salad.fail"), false);

        return null;
            }, new FoodComponent(6, 4, true)), new Settings()
            .useCooldown(5f)
            .rarity(Rarity.RARE)
    );

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        this.finishUsingFunction.finishUsing(stack, world, user);
        return super.finishUsing(stack, world, user);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        switch (stack.getItem().toString()) {
            case "socwars:shrinking_pills" -> textConsumer.accept(Text.translatable("tooltip.shrinking_pills"));
            case "socwars:biggening_pills" -> textConsumer.accept(Text.translatable("tooltip.biggening_pills"));
            case "socwars:red_shell" -> textConsumer.accept(Text.translatable("tooltip.red_shell"));
        }
    }
}
