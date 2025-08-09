package com.soc.items;

import com.soc.entities.BigTnt;
import com.soc.items.util.ModItems;
import com.soc.materials.ToolMaterials;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.world.World;

import static com.soc.items.util.ModItems.addItemToGroups;

public class Devastator extends Item {
    private final boolean isPrime;

    public Devastator(final Item.Settings settings, final boolean isPrime) {
        super(settings);
        this.isPrime = isPrime;
    }

    static LocalRandom random = new LocalRandom(0);

    public static void initialise() {
        addItemToGroups(DEVASTATOR, ItemGroups.COMBAT);
        addItemToGroups(DEVASTATOR_PRIME, ItemGroups.COMBAT);
    }

    public static final Item DEVASTATOR = ModItems.register("devastator", (settings) -> new Devastator(settings, false), new Settings()
            .rarity(Rarity.RARE)
            .sword(ToolMaterials.DEVASTATOR_TOOL_MATERIAL, 2f, -3.7f)
            .component(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(false, ReferenceSortedSets.emptySet()))
    );
    public static final Item DEVASTATOR_PRIME = ModItems.register("devastator_prime", (settings) -> new Devastator(settings, true), new Settings()
            .rarity(Rarity.EPIC)
            .sword(ToolMaterials.DEVASTATOR_TOOL_MATERIAL, 2f, -3.7f)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .component(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(false, ReferenceSortedSets.emptySet()))
    );

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.isPrime) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 3 * 20, 1));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3 * 20, 2));
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        world.spawnEntity(new BigTnt(world, user.getPos(), user, this.isPrime ? 20f : 13f));
        return ActionResult.SUCCESS;
    }
}
