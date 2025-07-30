package com.soc.items;

import com.soc.SocWars;
import com.soc.items.util.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static com.soc.items.util.ModItems.addItemToGroups;

public class NetherightSword extends Item {
    private final boolean isWrong;

    public static final RegistryKey<DamageType> NETHERWRONG_SWORD_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SocWars.MOD_ID, "netherwrong_sword_damage"));

    public NetherightSword(final Settings settings, final boolean isWrong) {
        super(settings);
        this.isWrong = isWrong;
    }

    public static void initialise() {
        addItemToGroups(NETHERIGHT_SWORD, ItemGroups.COMBAT);
        addItemToGroups(NETHERWRONG_SWORD, ItemGroups.COMBAT);
    }

    public static final Item NETHERIGHT_SWORD = ModItems.register("netheright_sword", (settings) -> new NetherightSword(settings, false), new Settings()
            .sword(ToolMaterial.NETHERITE, -4.5f, 12f)
    );
    public static final Item NETHERWRONG_SWORD = ModItems.register("netherwrong_sword", (settings) -> new NetherightSword(settings, true), new Settings()
            .sword(ToolMaterial.NETHERITE, -3f, -3f)
    );

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (isWrong) {
            World world = attacker.getWorld();
            if (attacker instanceof LivingEntity && world instanceof ServerWorld serverWorld) {
                DamageSource damageSource = new DamageSource(
                        world.getRegistryManager()
                                .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(Lifethief.LIFETHIEF_DAMAGE.getValue()).get()
                );
                attacker.damage(serverWorld, damageSource, 7.5f);
            }
        }
    }
}
