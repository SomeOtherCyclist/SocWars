package com.soc.items.util;

import com.soc.effects.util.ModEffects;
import com.soc.items.UseFunctionWeapon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class GravityOrb extends Item {
	public GravityOrb(Settings settings) {
		super(settings);
	}

	public static void initialise() {
		addItemToGroupsAndBaseItemGroup(GRAVITY_ORB, ItemGroups.TOOLS);
	}

	public static final Item GRAVITY_ORB = ModItems.register("gravity_orb", GravityOrb::new, new Settings()
			.useCooldown(7.5f)
			.rarity(Rarity.UNCOMMON)
	);

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		target.addStatusEffect(new StatusEffectInstance(ModEffects.ANTI_GRAVITY, (int) 7.5 * 20, 2, false, false));
		attacker.getStackInHand(Hand.MAIN_HAND).decrementUnlessCreative(1, attacker);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		user.addStatusEffect(new StatusEffectInstance(ModEffects.ANTI_GRAVITY, (int) 7.5 * 20, 2, false, false));
		user.getStackInHand(hand).decrementUnlessCreative(1, user);

		return ActionResult.SUCCESS;
	}
}
