package com.soc.items;

import com.soc.items.util.EffectRecord;
import com.soc.items.util.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import static com.soc.items.util.ModItems.addItemToGroups;
import static net.minecraft.item.Items.*;
import static org.apache.commons.io.IOUtils.length;

public class DiceOfFate extends Item {
    private static final Item[] RANDOM_RINGS = {
        InvisibilityRing.INVISIBILITY_RING,
        PotionRing.LESSER_SPEED_RING,
        PotionRing.GREATER_SPEED_RING,
        PotionRing.LESSER_JUMP_RING,
        PotionRing.GREATER_JUMP_RING
    };

    private final EffectRecord[] GARBO_EFFECTS = {
            new EffectRecord(StatusEffects.SLOWNESS, 3),
            new EffectRecord(StatusEffects.MINING_FATIGUE, 1),
            new EffectRecord(StatusEffects.NAUSEA, 0),
            new EffectRecord(StatusEffects.BLINDNESS, 0),
            new EffectRecord(StatusEffects.HUNGER, 0),
            new EffectRecord(StatusEffects.WEAKNESS, 3),
            new EffectRecord(StatusEffects.POISON, 1),
            new EffectRecord(StatusEffects.GLOWING, 0),
    };

    public DiceOfFate(Item.Settings settings) {
        super(settings);
    }

    static LocalRandom random = new LocalRandom(0);

    public static void initialise() {
        addItemToGroups(DICE_OF_FATE);
    }

    public static final Item DICE_OF_FATE = ModItems.register("dice_of_fate", DiceOfFate::new, new Settings()
            .maxCount(1)
            .rarity(Rarity.EPIC)
    );

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        int randomCase = random.nextBetween(1, 6);

        if (world.isClient() && randomCase != 5) {
            return ActionResult.SUCCESS;
        }

        user.sendMessage(Text.literal("The dice of fate lands on a " + randomCase + "!"), true);
        switch (randomCase) {
            case 1:
                user.setStackInHand(hand, NETHERITE_SWORD.getDefaultStack());
                break;
            case 2:
                user.setStackInHand(hand, ENCHANTED_GOLDEN_APPLE.getDefaultStack());
                break;
            case 3:
                user.setStackInHand(hand, ItemStack.EMPTY);
                explode(world, user);
                break;
            case 4:
                user.setStackInHand(hand, RANDOM_RINGS[random.nextBetween(0, length(RANDOM_RINGS) - 1)].getDefaultStack());
                break;
            case 5:
                user.setStackInHand(hand, ItemStack.EMPTY);
                user.addVelocity(0, 1.5f, 0);
                break;
            case 6:
                user.setStackInHand(hand, ItemStack.EMPTY);
                for (EffectRecord effectRecord : GARBO_EFFECTS) {
                    user.addStatusEffect(new StatusEffectInstance(effectRecord.effect(), 20 * 20, effectRecord.amplifier(), false, true, true));
                }
                break;
        }

        return ActionResult.FAIL;
    }

    private static void explode(World world, PlayerEntity user) {
        if (world instanceof ServerWorld serverWorld) {
            if (serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1, 1, false, false, false));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1, 11, false, false, false));
                world.createExplosion(null, Explosion.createDamageSource(world, null), null, user.getX(), user.getY(), user.getZ(), 8.0f, false, World.ExplosionSourceType.TNT);
            }
        }
    }
}
