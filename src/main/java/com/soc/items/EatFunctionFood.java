package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.items.util.StopUsingFunction;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import static com.soc.items.util.ModItems.addItemToGroups;
import static com.soc.lib.SocWarsLib.SQRT2;
import static com.soc.lib.SocWarsLib.scaleEntity;

public class EatFunctionFood extends Item {
    private final StopUsingFunction stopUsingFunction;

    public EatFunctionFood(final Item.Settings settings, final StopUsingFunction stopUsingFunction) {
        super(settings.food(new FoodComponent(0, 1, false)));
        this.stopUsingFunction = stopUsingFunction;
    }
    /*
    public EatFunctionFood(final Item.Settings settings, final StopUsingFunction stopUsingFunction) {
        this(settings, stopUsingFunction);
    }
     */

    public static void initialise() {
        addItemToGroups(SHRINKING_PILLS, ItemGroups.FOOD_AND_DRINK);
        addItemToGroups(BIGGENING_PILLS, ItemGroups.FOOD_AND_DRINK);
    }

    public static final Item SHRINKING_PILLS = ModItems.register("shrinking_pills", settings -> new EatFunctionFood(settings, (stack, world, user, remaining) -> {
                scaleEntity(user, SQRT2 * 0.5f);
                return !user.isInCreativeMode();
    }), new Settings()
            .rarity(Rarity.UNCOMMON));
    public static final Item BIGGENING_PILLS = ModItems.register("biggening_pills", settings -> new EatFunctionFood(settings, (stack, world, user, remaining) -> {
                scaleEntity(user, SQRT2);
                return !user.isInCreativeMode();
    }), new Settings()
            .rarity(Rarity.UNCOMMON));

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        return stack.copyWithCount(stopUsingFunction.stopUsing(stack, world, user, 0) ? stack.getCount() - 1 : stack.getCount());
    }
}
