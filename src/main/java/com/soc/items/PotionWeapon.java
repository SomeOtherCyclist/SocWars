package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.tools.ToolMaterials;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.random.LocalRandom;

import java.util.List;

import static com.soc.items.util.ModItems.addItemToGroups;

public class PotionWeapon extends Item {
    private final RegistryEntry<StatusEffect> effect;
    private final int amplifier;
    private final int duration;
    private final int reciprocalChance;

    private static final LocalRandom random = new LocalRandom(0);

    public PotionWeapon(Settings settings, RegistryEntry<StatusEffect> effect, int amplifier, int duration, int reciprocalChance) {
        super(settings);
        this.effect = effect;
        this.amplifier = amplifier;
        this.duration = duration;
        this.reciprocalChance = reciprocalChance;
    }

    public static void initialise() {
        addItemToGroups(BIOHACKER, ItemGroups.COMBAT);
        addItemToGroups(GOO_SHOVEL, ItemGroups.COMBAT);
    }

    public static final Item BIOHACKER = ModItems.register("biohacker", (settings) -> new PotionWeapon(settings, StatusEffects.POISON, 1, 3 * 20, 4), new Settings()
            .sword(ToolMaterials.POTIONWEAPON_TOOL_MATERIAL, 2.5f, -2.2f)
            .maxDamage(300)
    );
    public static final Item GOO_SHOVEL = ModItems.register("goo_shovel", (settings) -> new PotionWeapon(settings, StatusEffects.SLOWNESS, 1, 1 * 20, 1), new Settings()
            .sword(ToolMaterials.POTIONWEAPON_TOOL_MATERIAL, -2f, -3f)
            .component(DataComponentTypes.TOOL, new ToolComponent(List.of(ToolComponent.Rule.ofNeverDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.SHOVEL_MINEABLE)), ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.SHOVEL_MINEABLE), 6.5f)), 1.0F, 1, true))
            .maxDamage(20) //The code above is definitely okay and not terrible whatsoever
    );

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (random.nextBetween(1, this.reciprocalChance) == 1) {
            target.addStatusEffect(new StatusEffectInstance(this.effect, this.duration, this.amplifier));
        }
    }
}
