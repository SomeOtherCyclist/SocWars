package com.soc.items;

import com.soc.SocWars;
import com.soc.items.util.ModItems;
import com.soc.materials.ToolMaterials;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.world.World;

import static com.soc.items.util.ModItems.addItemToGroups;

public class Lifethief extends Item {
    public Lifethief(Settings settings) {
        super(settings);
    }

    static LocalRandom random = new LocalRandom(0);

    public static final RegistryKey<DamageType> LIFETHIEF_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SocWars.MOD_ID, "lifethief"));

    public static void initialise() {
        addItemToGroups(LIFETHIEF, ItemGroups.COMBAT);
    }

    public static final Item LIFETHIEF = ModItems.register("lifethief", Lifethief::new, new Settings()
            .useCooldown(3.5f)
            .sword(ToolMaterials.LIFETHIEF_TOOL_MATERIAL, 2.5f, -2.2f)
            .component(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(false, ReferenceSortedSets.emptySet()))
    );

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int bonusHealth = random.nextBetween(-1, 2);


        if (bonusHealth >= 0) {
            attacker.setHealth(Math.min(attacker.getMaxHealth(), attacker.getHealth() + bonusHealth));
        } else {
            World world = attacker.getWorld();
            if (attacker instanceof LivingEntity && world instanceof ServerWorld serverWorld) {
                DamageSource damageSource = new DamageSource(
                        world.getRegistryManager()
                                .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(LIFETHIEF_DAMAGE.getValue()).get()
                );
                attacker.damage(serverWorld, damageSource, 1.0f);
            }
        }
    }
}
