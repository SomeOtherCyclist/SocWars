package com.soc.items;

import com.soc.items.util.EffectRecord;
import com.soc.items.util.ModItems;
import com.soc.materials.ToolMaterials;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.LocalRandom;

import java.util.List;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class PotionWeapon extends Item {
    private final EffectRecipients effectRecipients;
    private final EffectRecord[] effects;

    private static final EffectRecord[] ALCHEMISM_EFFECTS = {
            new EffectRecord(StatusEffects.INSTANT_DAMAGE, 0, 1),
            new EffectRecord(StatusEffects.INSTANT_HEALTH, 0, 1),
            new EffectRecord(StatusEffects.POISON, 1, 1 * 20),
            new EffectRecord(StatusEffects.WITHER, 1, 1 * 20),
            new EffectRecord(StatusEffects.SPEED, 0, 1 * 20),
            new EffectRecord(StatusEffects.REGENERATION, 1, 1 * 20),
            new EffectRecord(StatusEffects.BLINDNESS, 0, 1 * 20),
            new EffectRecord(StatusEffects.NAUSEA, 0, 1 * 20),
            new EffectRecord(StatusEffects.JUMP_BOOST, 1, 1 * 20),
            new EffectRecord(StatusEffects.DARKNESS, 0, 1 * 20),
            new EffectRecord(StatusEffects.SLOW_FALLING, 0, 1 * 20),
            new EffectRecord(StatusEffects.RESISTANCE, 1, 1 * 20),
    };

    private static final LocalRandom random = new LocalRandom(0);

    public enum EffectRecipients {
        TARGET,
        ATTACKER,
        POSITIVE_BOTH,
        BOTH
    }

    public PotionWeapon(final Settings settings, final EffectRecipients effectRecipients, final EffectRecord... effects) {
        super(settings);
        this.effectRecipients = effectRecipients;
        this.effects = effects;
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(BIOHACKER, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(GOO_SHOVEL, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(STATUE_BLADE, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(ALCHEMISM, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(RED_SWORD, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(PURPLE_SWORD, ItemGroups.COMBAT);
    }

    public static final Item BIOHACKER = ModItems.register("biohacker", settings -> new PotionWeapon(settings, EffectRecipients.TARGET, new EffectRecord(StatusEffects.POISON, 1, 3 * 20, 4)), new Settings()
            .sword(ToolMaterials.POTIONWEAPON, 2.5f, -2.2f)
            .rarity(Rarity.RARE)
            .maxDamage(300)
    );
    public static final Item GOO_SHOVEL = ModItems.register("goo_shovel", settings -> new PotionWeapon(settings, EffectRecipients.TARGET, new EffectRecord(StatusEffects.SLOWNESS, 1, 1 * 20, 1)), new Settings()
            .sword(ToolMaterials.POTIONWEAPON, -2f, -3f)
            .component(DataComponentTypes.TOOL, new ToolComponent(List.of(ToolComponent.Rule.ofNeverDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)), ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.SHOVEL_MINEABLE), 6.5f)), 1.0f, 1, true))
            .rarity(Rarity.UNCOMMON)
            .maxDamage(20) //The code above is definitely okay and not terrible whatsoever
    );
    public static final Item STATUE_BLADE = ModItems.register("statue_blade", settings -> new PotionWeapon(settings, EffectRecipients.BOTH, new EffectRecord(StatusEffects.SLOWNESS, 6, 3 * 20, 1)), new Settings()
            .sword(ToolMaterials.POTIONWEAPON, 3.5f, -2.3f)
            .rarity(Rarity.RARE)
            .maxDamage(300)
    );
    public static final Item ALCHEMISM = ModItems.register("alchemism", settings -> new PotionWeapon(settings, EffectRecipients.POSITIVE_BOTH, ALCHEMISM_EFFECTS), new Settings()
            .sword(ToolMaterials.POTIONWEAPON, 4f, -2.5f)
            .rarity(Rarity.EPIC)
            .maxDamage(500)
    );
    public static final Item RED_SWORD = ModItems.register("red_sword", settings -> new PotionWeapon(settings, EffectRecipients.TARGET, new EffectRecord(StatusEffects.WEAKNESS, 0, 5 * 20, 1)), new Settings()
            .sword(ToolMaterials.POTIONWEAPON, 1f, -2.1f)
            .maxDamage(400)
    );
    public static final Item PURPLE_SWORD = ModItems.register("purple_sword", settings -> new PotionWeapon(settings, EffectRecipients.ATTACKER, new EffectRecord(StatusEffects.INVISIBILITY, 0, 1 * 20, 1)), new Settings()
            .sword(ToolMaterials.POTIONWEAPON, 1f, -2.2f)
            .rarity(Rarity.RARE)
            .maxDamage(900)
    );

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        EffectRecord effect = this.effects[random.nextBetween(0, this.effects.length - 1)];

        if (random.nextBetween(1, effect.reciprocalChance()) == 1) {
            switch (this.effectRecipients) {
                case TARGET:
                    target.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier()));
                    break;
                case ATTACKER:
                    attacker.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier()));
                    break;
                case POSITIVE_BOTH:
                    target.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier()));
                    if (effect.effect().value().isBeneficial()) {
                        attacker.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier()));
                    }
                    break;
                case BOTH:
                    target.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier()));
                    attacker.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier()));
                    break;
            }
        }
    }
}
