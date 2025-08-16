package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.items.util.RingItem;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import net.minecraft.entity.effect.StatusEffectInstance;

import static com.soc.items.util.ModItems.addItemToGroups;

public class PotionRing extends RingItem {
    private final RegistryEntry<StatusEffect> effect;
    private final int amplifier;

    public PotionRing(final Settings settings, final RegistryEntry<StatusEffect> effect, final int amplifier) {
        super(settings);
        this.effect = effect;
        this.amplifier = amplifier;
    }

    public static void initialise() {
        addItemToGroups(LESSER_SPEED_RING, ItemGroups.TOOLS);
        addItemToGroups(GREATER_SPEED_RING, ItemGroups.TOOLS);
        addItemToGroups(LESSER_JUMP_RING, ItemGroups.TOOLS);
        addItemToGroups(GREATER_JUMP_RING, ItemGroups.TOOLS);
    }

    public static final Item LESSER_SPEED_RING = ModItems.register("lesser_speed_ring", (settings) -> new PotionRing(settings, StatusEffects.SPEED, 0), new Settings().maxDamage(20 * 30).rarity(Rarity.UNCOMMON));
    public static final Item GREATER_SPEED_RING = ModItems.register("greater_speed_ring", (settings) -> new PotionRing(settings, StatusEffects.SPEED, 1), new Settings().maxDamage(20 * 20).rarity(Rarity.UNCOMMON));
    public static final Item LESSER_JUMP_RING = ModItems.register("lesser_jump_ring", (settings) -> new PotionRing(settings, StatusEffects.JUMP_BOOST, 1), new Settings().maxDamage(20 * 15).rarity(Rarity.UNCOMMON));
    public static final Item GREATER_JUMP_RING = ModItems.register("greater_jump_ring", (settings) -> new PotionRing(settings, StatusEffects.JUMP_BOOST, 3), new Settings().maxDamage(20 * 5).rarity(Rarity.UNCOMMON));

    @Override
    protected void ringUse(World world, PlayerEntity user, Hand hand) {
        user.addStatusEffect(new StatusEffectInstance( this.effect, 5, amplifier, false, false, false));
    }
}
