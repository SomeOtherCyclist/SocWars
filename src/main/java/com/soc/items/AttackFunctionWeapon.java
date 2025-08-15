package com.soc.items;

import com.soc.SocWars;
import com.soc.items.util.DamageTypes;
import com.soc.items.util.ModItems;
import com.soc.items.util.AttackFunction;
import com.soc.materials.ToolMaterials;
import com.soc.util.Sounds;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.world.World;

import java.util.function.Consumer;

import static com.soc.items.util.ModItems.addItemToGroups;

public class AttackFunctionWeapon extends Item {
    private final AttackFunction attackFunction;

    public AttackFunctionWeapon(Settings settings, AttackFunction attackFunction) {
        super(settings);
        this.attackFunction = attackFunction;
    }

    static LocalRandom random = new LocalRandom(0);

    public static void initialise() {
        addItemToGroups(LIFETHIEF, ItemGroups.COMBAT);
        addItemToGroups(DEVASTATOR_PRIME, ItemGroups.COMBAT);
        addItemToGroups(NETHERWRONG_SWORD, ItemGroups.COMBAT);
        addItemToGroups(ORANGE_SWORD, ItemGroups.COMBAT);
        addItemToGroups(BLUE_SWORD, ItemGroups.COMBAT);
        addItemToGroups(PINK_SWORD, ItemGroups.COMBAT);
        addItemToGroups(KNOCKFORWARD_SWORD, ItemGroups.COMBAT);
        addItemToGroups(STORMAGEDDON, ItemGroups.COMBAT);
    }

    public static final Item LIFETHIEF = ModItems.register("lifethief", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                int bonusHealth = random.nextBetween(-1, 2);

                if (bonusHealth >= 0) {
                    attacker.setHealth(Math.min(attacker.getMaxHealth(), attacker.getHealth() + bonusHealth));
                } else {
                    World world = attacker.getWorld();
                    if (attacker instanceof LivingEntity && world instanceof ServerWorld serverWorld) {
                        DamageSource damageSource = new DamageSource(
                                world.getRegistryManager()
                                        .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                                        .getEntry(DamageTypes.LIFETHIEF.getValue()).get()
                        );
                        attacker.damage(serverWorld, damageSource, 1.0f);
                    }
                }
            }), new Settings()
            .sword(ToolMaterials.LIFETHIEF, 2.5f, -2.2f)
            .component(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(false, ReferenceSortedSets.emptySet()))
    );
    public static final Item DEVASTATOR_PRIME = ModItems.register("devastator_prime", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 3 * 20, 1));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3 * 20, 2));
            }), new Settings()
            .rarity(Rarity.EPIC)
            .sword(ToolMaterials.DEVASTATOR, 2f, -3.7f)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
    );
    public static final Item NETHERWRONG_SWORD = ModItems.register("netherwrong_sword", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                World world = attacker.getWorld();
                if (attacker instanceof LivingEntity && world instanceof ServerWorld serverWorld) {
                    DamageSource damageSource = new DamageSource(
                            world.getRegistryManager()
                                    .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                                    .getEntry(DamageTypes.LIFETHIEF.getValue()).get()
                    );
                    attacker.damage(serverWorld, damageSource, 7.5f);
                }
            }), new Settings()
            .sword(ToolMaterial.NETHERITE, -3f, -3f)
    );
    public static final Item ORANGE_SWORD = ModItems.register("orange_sword", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                attacker.getWorld().playSound(null, target.getBlockPos(), Sounds.VINE_BOOM, SoundCategory.MASTER, 1f, 1f);
            }), new Settings()
            .sword(ToolMaterials.BASE, 5f, -2.1f)
            .maxDamage(500)
    );
    public static final Item BLUE_SWORD = ModItems.register("blue_sword", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                World world = attacker.getWorld();
                if (world instanceof ServerWorld serverWorld) {
                    SlimeEntity slime = new SlimeEntity(EntityType.SLIME, world);
                    slime.setSize(2, true);
                    slime.setPosition(target.getPos());
                    serverWorld.spawnEntity(slime);
                }
            }), new Settings()
            .rarity(Rarity.UNCOMMON)
            .sword(ToolMaterials.BASE, 8f, -2.15f)
            .maxDamage(800)
    );
    public static final Item PINK_SWORD = ModItems.register("pink_sword", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                attacker.setAbsorptionAmount(attacker.getAbsorptionAmount() + 1);
            }), new Settings()
            .rarity(Rarity.EPIC)
            .sword(ToolMaterials.BASE, 10f, -2.2f)
            .maxDamage(1000)
    );
    public static final Item KNOCKFORWARD_SWORD = ModItems.register("knockforward_sword", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                target.addVelocity(attacker.getRotationVector().multiply(-0.8f));
            }), new Settings()
            .rarity(Rarity.UNCOMMON)
            .sword(ToolMaterials.BASE, 5.5f, -2.2f)
            .maxDamage(450)
    );
    public static final Item STORMAGEDDON = ModItems.register("stormageddon", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, target.getWorld());
                lightning.setPosition(target.getPos());
                target.getWorld().spawnEntity(lightning);
            }), new Settings()
            .rarity(Rarity.UNCOMMON)
            .sword(ToolMaterials.BASE, 5f, -2.1f)
            .maxDamage(500)
    );

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attackFunction.attack(stack, target, attacker);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        Text text = switch (stack.getItem().toString()) {
            case "socwars:knockforward_sword" -> Text.literal("The Hypixel special").formatted(Formatting.GOLD);
            case "socwars:stormageddon" -> Text.literal("He speaks baby");
            default -> null;
        };

        if (text != null) {
            textConsumer.accept(text);
        }
    }
}
