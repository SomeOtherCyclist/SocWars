package com.soc.items;

import com.soc.items.util.DamageTypes;
import com.soc.items.util.ModItems;
import com.soc.items.util.AttackFunction;
import com.soc.items.util.ModifyEquipmentFunction;
import com.soc.materials.ToolMaterials;
import com.soc.util.Sounds;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Consumer;

import static com.soc.items.util.ModItems.addItemToGroups;

public class AttackFunctionWeapon extends Item {
    private final AttackFunction attackFunction;

    private static final EquipmentSlot[] ARMOUR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static Item leatherArmour(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> Items.LEATHER_HELMET;
            case CHEST -> Items.LEATHER_CHESTPLATE;
            case LEGS -> Items.LEATHER_LEGGINGS;
            case FEET -> Items.LEATHER_BOOTS;
            default -> null; //Unreachable
        };
    }
    private static RegistryEntry<Enchantment> enchantmentEntry(World world, RegistryKey<Enchantment> enchantmentKey) {
        return world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.BINDING_CURSE);
    }
    private enum ReplaceMode {
        PRESENT,
        NOT_LEATHER
    }

    public AttackFunctionWeapon(Settings settings, AttackFunction attackFunction) {
        super(settings);
        this.attackFunction = attackFunction;
    }

    public static void initialise() {
        addItemToGroups(LIFETHIEF, ItemGroups.COMBAT);
        addItemToGroups(DEVASTATOR_PRIME, ItemGroups.COMBAT);
        addItemToGroups(NETHERWRONG_SWORD, ItemGroups.COMBAT);
        addItemToGroups(ORANGE_SWORD, ItemGroups.COMBAT);
        addItemToGroups(BLUE_SWORD, ItemGroups.COMBAT);
        addItemToGroups(PINK_SWORD, ItemGroups.COMBAT);
        addItemToGroups(KNOCKFORWARD_SWORD, ItemGroups.COMBAT);
        addItemToGroups(TRANSPORTAS, ItemGroups.COMBAT);
        addItemToGroups(STORMAGEDDON, ItemGroups.COMBAT);
        addItemToGroups(DETONATOR, ItemGroups.COMBAT);
        addItemToGroups(SHATTERSTAR, ItemGroups.COMBAT);
        addItemToGroups(LEATHERER, ItemGroups.COMBAT);
        addItemToGroups(SPRING_SWORD, ItemGroups.COMBAT);
    }

    public static final Item LIFETHIEF = ModItems.register("lifethief", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                int bonusHealth = attacker.getWorld().random.nextBetween(-1, 2);

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
            .rarity(Rarity.RARE)
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
    public static final Item TRANSPORTAS = ModItems.register("transportas", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                if (target.getWorld().random.nextBetween(1, 4) == 1) {
                    target.setPosition(target.getPos().add(target.getRotationVector().multiply(3f)));
                }
            }), new Settings()
            .rarity(Rarity.RARE)
            .sword(ToolMaterials.BASE, 5.5f, -2f)
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
    public static final Item DETONATOR = ModItems.register("detonator", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                TntEntity tnt = new TntEntity(target.getWorld(), target.getX(), target.getY(), target.getZ(), attacker);
                tnt.setFuse(4);

                target.getWorld().spawnEntity(tnt);
            }), new Settings()
            .rarity(Rarity.RARE)
            .sword(ToolMaterials.BASE, 4.5f, -2.2f)
            .maxDamage(400)
    );
    public static final Item SHATTERSTAR = ModItems.register("shatterstar", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                modifyEquipment(target, attacker, ReplaceMode.PRESENT, (targetEntity, slot) -> {
                    target.equipStack(slot, ItemStack.EMPTY);
                }, (targetEntity, slot) -> {
                    target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                });
            }), new Settings()
            .sword(ToolMaterials.BASE, -1f, -1f)
            .maxDamage(0)
            .rarity(Rarity.RARE)
    );
    public static final Item LEATHERER = ModItems.register("leatherer", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                modifyEquipment(target, attacker, ReplaceMode.NOT_LEATHER, (targetEntity, slot) -> {
                    ItemStack item = leatherArmour(slot).getDefaultStack();
                    item.addEnchantment(enchantmentEntry(target.getWorld(), Enchantments.BINDING_CURSE), 1);
                    item.addEnchantment(enchantmentEntry(target.getWorld(), Enchantments.VANISHING_CURSE), 1);
                    item.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE).build());

                    target.equipStack(slot, item);
                }, null);
            }), new Settings()
            .sword(ToolMaterials.BASE, -1f, -1f)
            .maxDamage(0)
            .rarity(Rarity.RARE)
    );
    public static final Item SPRING_SWORD = ModItems.register("spring_sword", (settings) -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                if (attacker.getWorld().random.nextBetween(1, attacker.isOnGround() ? 8 : 6) == 1) {
                    ItemEntity item = attacker.dropItem(stack, true, false);
                    item.setVelocity(attacker.getRotationVector().multiply(0.3f));

                    attacker.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                }
            }), new Settings()
            .sword(ToolMaterials.BASE, 4f, -1f)
            .maxDamage(350)
            .rarity(Rarity.UNCOMMON)
    );

    private static void modifyEquipment(LivingEntity target, LivingEntity attacker, ReplaceMode replaceMode, ModifyEquipmentFunction armourFunction, ModifyEquipmentFunction handFunction) {
        ArrayList<EquipmentSlot> armour = new ArrayList<>();
        for (EquipmentSlot slot : ARMOUR_SLOTS) {
            if (replaceMode == ReplaceMode.PRESENT ? target.hasStackEquipped(slot) : target.getEquippedStack(slot).getItem() != leatherArmour(slot)) { //Maybe lambda-ify this
                armour.add(slot);
            }
        }

        if (!armour.isEmpty() || target.getStackInHand(Hand.MAIN_HAND) != ItemStack.EMPTY) {
            target.playSound(SoundEvents.ENTITY_ITEM_BREAK.value());
            attacker.playSound(SoundEvents.ENTITY_ITEM_BREAK.value());

            attacker.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }

        if (!armour.isEmpty()) {
            armourFunction.modifyEquipment(target, armour.get(target.getWorld().random.nextBetween(0, armour.size() - 1)));
        } else {
            handFunction.modifyEquipment(target, null);
        }
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attackFunction.attack(stack, target, attacker);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        switch (stack.getItem().toString()) {
            case "socwars:knockforward_sword" -> textConsumer.accept(Text.literal("The Hypixel special").formatted(Formatting.GOLD));
            case "socwars:stormageddon" -> textConsumer.accept(Text.literal("He speaks baby"));
            case "socwars:spring_sword" -> {
                textConsumer.accept(Text.literal("Potentially charged").formatted(Formatting.YELLOW));
                textConsumer.accept(Text.literal("Potentially charged").formatted(Formatting.YELLOW));
            }
        };
    }
}
