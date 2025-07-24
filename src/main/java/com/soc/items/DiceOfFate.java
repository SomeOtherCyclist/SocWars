package com.soc.items;

import com.soc.items.util.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.world.World;

import static com.soc.items.util.ModItems.addItemToGroups;

public class DiceOfFate extends Item {
    public DiceOfFate(Item.Settings settings) {
        super(settings);
    }

    static LocalRandom random = new LocalRandom(0);

    public static void initialise() {
        addItemToGroups(DICE_OF_FATE, ItemGroups.COMBAT);
    }

    public static final Item DICE_OF_FATE = ModItems.register("lifethief", Lifethief::new, new Settings());

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        switch (random.nextBetween(1, 6)) {
            case 1:
                    break;
            case 2:
                    break;
            case 3:
                    break;
            case 4:
                    break;
            case 5:
                    break;
            case 6:
                    break;
        }

        

        return ActionResult.FAIL;
    }
}
