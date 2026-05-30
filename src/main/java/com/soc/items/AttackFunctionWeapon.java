package com.soc.items;

import com.soc.SocWars;
import com.soc.effects.util.ModEffects;
import com.soc.game.manager.GamesManager;
import com.soc.game.manager.HideAndSeekGameManager;
import com.soc.util.BlockTags;
import com.soc.util.DamageTypes;
import com.soc.items.util.ModItems;
import com.soc.items.util.AttackFunction;
import com.soc.items.util.ModifyEquipmentFunction;
import com.soc.materials.ToolMaterials;
import com.soc.util.Sounds;
import com.soc.util.SphereExplosion;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static com.soc.lib.SocWarsLib.*;
import static com.soc.util.SphereExplosion.fireExplosion;

public class AttackFunctionWeapon extends Item {
    private enum ReplaceMode {
        PRESENT,
        NOT_LEATHER
    }

    private final AttackFunction attackFunction;

    public AttackFunctionWeapon(Settings settings, AttackFunction attackFunction) {
        super(settings);
        this.attackFunction = attackFunction;
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(LIFETHIEF, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(DEVASTATOR_PRIME, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(NETHERWRONG_SWORD, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(ORANGE_SWORD, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(BLUE_SWORD, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(PINK_SWORD, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(KNOCKFORWARD_SWORD, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(TRANSPORTAS, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(STORMAGEDDON, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(DETONATOR, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(SHATTERSTAR, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(LEATHERER, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(SPRING_SWORD, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(FLESHY_BLADE, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(FIRESTORM, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(CORRUPTED_SWORD, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(POSTURA, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(FULL_METAL_SWORD, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(SEEKING_STICK, ItemGroups.COMBAT);
    }

    public static final Item LIFETHIEF = ModItems.register("lifethief", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                final int bonusHealth = attacker.getWorld().random.nextBetween(-1, 2);

                if (bonusHealth >= 0) {
                    attacker.heal(bonusHealth);
                } else {
                    final World world = attacker.getWorld();
                    if (attacker instanceof LivingEntity && world instanceof ServerWorld serverWorld) {
                        final DamageSource damageSource = damageSource(world, DamageTypes.LIFETHIEF);
                        attacker.damage(serverWorld, damageSource, 1.0f);
                    }
                }
            }), new Settings()
            .rarity(Rarity.RARE)
            .sword(ToolMaterials.LIFETHIEF, 2.5f, -2.2f)
    );
    public static final Item DEVASTATOR_PRIME = ModItems.register("devastator_prime", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 3 * 20, 1));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3 * 20, 2));
            }), new Settings()
            .rarity(Rarity.EPIC)
            .sword(ToolMaterials.DEVASTATOR, 14.5f, -2.9f)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
    );
    public static final Item NETHERWRONG_SWORD = ModItems.register("netherwrong_sword", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                final World world = attacker.getWorld();
                if (attacker instanceof LivingEntity && world instanceof ServerWorld serverWorld) {
                    final DamageSource damageSource = damageSource(world, DamageTypes.NETHERWRONG_SWORD);
                    attacker.damage(serverWorld, damageSource, 8f);
                }
            }), new Settings()
            .sword(ToolMaterial.NETHERITE, -3f, -3f)
    );
    public static final Item ORANGE_SWORD = ModItems.register("orange_sword", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                attacker.getWorld().playSound(null, attacker.getBlockPos(), Sounds.VINE_BOOM, SoundCategory.PLAYERS, 1f, 1f);
            }), new Settings()
            .sword(ToolMaterials.BASE, 5f, -2.1f)
            .maxDamage(500)
    );
    public static final Item BLUE_SWORD = ModItems.register("blue_sword", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                final World world = attacker.getWorld();
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
    public static final Item PINK_SWORD = ModItems.register("pink_sword", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                attacker.setAbsorptionAmount(attacker.getAbsorptionAmount() + 1);
            }), new Settings()
            .rarity(Rarity.EPIC)
            .sword(ToolMaterials.BASE, 10f, -2.2f)
            .maxDamage(1000)
    );
    public static final Item KNOCKFORWARD_SWORD = ModItems.register("knockforward_sword", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                final Vec3d velocity = attacker.getRotationVector().multiply(-1.25f);

                if (target instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer.getId(), serverPlayer.getVelocity().add(velocity)));
                } else {
                    target.addVelocity(velocity);
                }
            }), new Settings()
            .rarity(Rarity.UNCOMMON)
            .sword(ToolMaterials.BASE, 5.5f, -2.2f)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .maxDamage(450)
    );
    public static final Item TRANSPORTAS = ModItems.register("transportas", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                if (target.getWorld().random.nextBetween(1, 4) == 1) {
                    target.setPosition(target.getPos().add(target.getRotationVector().multiply(3f)));
                }
            }), new Settings()
            .rarity(Rarity.RARE)
            .sword(ToolMaterials.BASE, 5.5f, -2f)
            .maxDamage(450)
    );
    public static final Item STORMAGEDDON = ModItems.register("stormageddon", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                final LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, target.getWorld());
                lightning.setPosition(target.getPos());
                target.getWorld().spawnEntity(lightning);
            }), new Settings()
            .rarity(Rarity.UNCOMMON)
            .sword(ToolMaterials.BASE, 5f, -2.1f)
            .maxDamage(500)
    );
    public static final Item DETONATOR = ModItems.register("detonator", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                SphereExplosion.explode(target.getWorld(), target.getPos(), 3.5f, 1.5f, 0.4f, 0.25f, true, attacker, null);
            }), new Settings()
            .rarity(Rarity.RARE)
            .sword(ToolMaterials.BASE, 4.5f, -2.2f)
            .maxDamage(400)
    );
    public static final Item SHATTERSTAR = ModItems.register("shatterstar", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                modifyEquipment(target, attacker, ReplaceMode.PRESENT,
                        (targetEntity, slot) -> target.equipStack(slot, ItemStack.EMPTY),
                        (targetEntity, slot) -> target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY));
            }), new Settings()
            .maxCount(1)
            .rarity(Rarity.RARE)
    );
    public static final Item LEATHERER = ModItems.register("leatherer", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                modifyEquipment(target, attacker, ReplaceMode.NOT_LEATHER, (targetEntity, slot) -> {
                    ItemStack item = leatherArmour(slot).getDefaultStack();
                    item.addEnchantment(enchantmentEntry(target.getWorld(), Enchantments.BINDING_CURSE), 1);
                    item.addEnchantment(enchantmentEntry(target.getWorld(), Enchantments.VANISHING_CURSE), 1);
                    item.set(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE);

                    target.equipStack(slot, item);
                }, null);
            }), new Settings()
            .maxCount(1)
            .rarity(Rarity.RARE)
    );
    public static final Item SPRING_SWORD = ModItems.register("spring_sword", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                if (attacker.getWorld().random.nextBetween(1, attacker.isOnGround() ? 6 : 4) == 1) {
                    ItemEntity item = attacker.dropItem(stack, true, false);
                    item.setVelocity(attacker.getRotationVector().multiply(0.3f));

                    attacker.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                }
            }), new Settings()
            .sword(ToolMaterials.BASE, 8f, -1.2f)
            .maxDamage(350)
            .rarity(Rarity.RARE)
    );
    public static final Item FLESHY_BLADE = ModItems.register("fleshy_blade", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                attacker.getWorld().playSound(null, target.getBlockPos(), Sounds.FLESH, SoundCategory.MASTER, 1f, 1f);
            }), new Settings()
            .sword(ToolMaterials.BASE, 6f, -2.2f)
    );
    public static final Item FIRESTORM = ModItems.register("firestorm", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                final World world = target.getWorld();
                fireExplosion(world, target.getBlockPos(), 4f, attacker);
            }), new Settings()
            .sword(ToolMaterials.BASE, 5.5f, -2.5f)
            .maxDamage(400)
            .rarity(Rarity.RARE)
    );
    public static final Item CORRUPTED_SWORD = ModItems.register("corrupted_sword", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                final World world = target.getWorld();
                iterateInSphere(target.getBlockPos(), 4, 0, pos -> {
                    if (world.random.nextFloat() < 0.2f && !isBlockHidden(world, pos) && !world.getBlockState(pos).isIn(BlockTags.IMMUNE)) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                });
            }), new Settings()
            .sword(ToolMaterials.BASE, 5.5f, -2.3f)
            .maxDamage(550)
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item POSTURA = ModItems.register("postura", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                target.addStatusEffect(new StatusEffectInstance(ModEffects.ARTHRODESIS, 40 * 20, 0, false, true));
                stack.decrementUnlessCreative(1, attacker);
            }), new Settings()
            .maxCount(1)
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item FULL_METAL_SWORD = ModItems.register("full_metal_sword", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                attacker.getWorld().playSound(null, attacker.getBlockPos(), Sounds.GET_SOME, SoundCategory.PLAYERS, 1f, 1f);
            }) {
                @Override
                public DamageSource getDamageSource(LivingEntity user) {
                    return damageSource(user.getWorld(), DamageTypes.FULL_METAL_SWORD, user);
                }
            }, new Settings()
            .sword(ToolMaterials.BASE, 4.5f, -2.3f)
            .maxDamage(500)
    );
    public static final Item SEEKING_STICK = ModItems.register("seeking_stick", settings -> new AttackFunctionWeapon(settings, (stack, target, attacker) -> {
                GamesManager.getInstance().getGame(target).ifPresent(game -> {
                    if (game instanceof HideAndSeekGameManager hideAndSeekGameManager) {
                        hideAndSeekGameManager.tryFindPlayer(attacker, (ServerPlayerEntity)target);
                    }
                });
            }), new Settings()
            .rarity(Rarity.UNCOMMON)
            .attributeModifiers(new AttributeModifiersComponent(List.of(
                    new AttributeModifiersComponent.Entry(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_range"), 1d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND, AttributeModifiersComponent.Display.getDefault()),
                    new AttributeModifiersComponent.Entry(EntityAttributes.MOVEMENT_SPEED, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_speed"), 0.05d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND, AttributeModifiersComponent.Display.getHidden()),
                    new AttributeModifiersComponent.Entry(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_damage"), 6.5d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
            )))
    );

    private static void modifyEquipment(LivingEntity target, LivingEntity attacker, ReplaceMode replaceMode, ModifyEquipmentFunction armourFunction, ModifyEquipmentFunction handFunction) {
        final ArrayList<EquipmentSlot> armour = new ArrayList<>();
        for (EquipmentSlot slot : ARMOUR_SLOTS) {
            if (replaceMode == ReplaceMode.PRESENT ? target.hasStackEquipped(slot) : target.getEquippedStack(slot).getItem() != leatherArmour(slot)) { //Maybe lambda-ify this
                armour.add(slot);
            }
        }

        if (!armour.isEmpty() || target.getStackInHand(Hand.MAIN_HAND) != ItemStack.EMPTY) {
            target.playSound(SoundEvents.ENTITY_ITEM_BREAK.value());
            attacker.playSound(SoundEvents.ENTITY_ITEM_BREAK.value());

            attacker.getStackInHand(Hand.MAIN_HAND).decrementUnlessCreative(1, attacker);
        }

        if (!armour.isEmpty()) {
            armourFunction.modifyEquipment(target, armour.get(target.getWorld().random.nextBetween(0, armour.size() - 1)));
        } else {
            handFunction.modifyEquipment(target, null);
        }
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        this.attackFunction.attack(stack, target, attacker);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (stack.get(DataComponentTypes.WEAPON) == null) this.attackFunction.attack(stack, target, attacker);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        switch (stack.getItem().toString()) {
            case "socwars:knockforward_sword" -> {
                textConsumer.accept(Text.translatable("tooltip.knockforward_sword").formatted(Formatting.GOLD));
                textConsumer.accept(Text.translatable("tooltip.knockforward_sword.knockback").formatted(Formatting.GRAY));
            }
            case "socwars:stormageddon" -> textConsumer.accept(Text.translatable("tooltip.stormageddon"));
            case "socwars:spring_sword" -> textConsumer.accept(Text.translatable("tooltip.spring_sword").formatted(Formatting.YELLOW));
            case "socwars:fleshy_blade" -> textConsumer.accept(Text.translatable(ThrowableItem.getWorldTime() % 25 > 2 ? "tooltip.fleshy_blade" : "tooltip.fleshy_blade.wet").formatted(Formatting.RED));
            case "socwars:postura" -> textConsumer.accept(Text.translatable("tooltip.postura"));
            case "socwars:full_metal_sword" -> textConsumer.accept(Text.translatable("tooltip.full_metal_sword"));
            case "socwars:seeking_stick" -> textConsumer.accept(Text.translatable("tooltip.seeking_stick"));
        }
    }
}
