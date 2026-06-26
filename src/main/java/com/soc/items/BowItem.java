package com.soc.items;

import com.soc.SocWars;
import com.soc.game.manager.AbstractHidingGameManager;
import com.soc.game.manager.GamesManager;
import com.soc.items.util.ArrowFactory;
import com.soc.items.util.ModItems;
import com.soc.items.util.ScaledUseDuration;
import com.soc.util.DamageTypes;
import com.soc.util.SphereExplosion;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.soc.items.EatFunctionFood.CHORUS_SALAD_TRIES;
import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static com.soc.lib.SocWarsLib.*;
import static com.soc.util.SphereExplosion.fireExplosion;

public class BowItem extends RangedWeaponItem implements ScaledUseDuration {
    private final ArrowFactory<? extends ArrowEntity> arrowFactory;
    private final Function<ItemStack, Float> drawTime;
    private final Function<ItemStack, Float> speed;

    public BowItem(Settings settings, ArrowFactory<? extends ArrowEntity> arrowFactory, Function<ItemStack, Float> drawTime, Function<ItemStack, Float> speed) {
        super(settings.enchantable(2));
        this.arrowFactory = arrowFactory;
        this.drawTime = drawTime;
        this.speed = speed;
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(BOOM_BOW, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(MEGABOOM_BOW, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(FALCON_BOW, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(HEATER_BOW, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(CHORUS_BOW, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(CATASTROPHE_BOW, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(SEEKING_BOW, ItemGroups.COMBAT);
    }

    public static final Item BOOM_BOW = ModItems.register("boom_bow", settings -> new BowItem(settings, (world, user, projectileStack,weaponStack) -> new ArrowEntity(world, user, projectileStack, weaponStack) {
                @Override
                protected void onHit(LivingEntity target) {
                    super.onHit(target);
                    this.discard();
                    final Vec3d centre = target.getPos();
                    SphereExplosion.explode(world, centre, 4f, 1.5f, 0.7f, 0.7f, true, user, null);
                }

                @Override
                protected void onBlockHit(BlockHitResult blockHitResult) {
                    super.onBlockHit(blockHitResult);
                    this.discard();
                    final Vec3d centre = blockHitResult.getPos();
                    SphereExplosion.explode(world, centre, 4.5f, 1.5f, 0.9f, 0.8f, true, user, null);
                }
    }, stack -> 1.5f, stack -> 2.75f), new Settings()
            .rarity(Rarity.UNCOMMON)
            .maxDamage(30)
    );
    public static final Item MEGABOOM_BOW = ModItems.register("megaboom_bow", settings -> new BowItem(settings, (world, user, projectileStack,weaponStack) -> new ArrowEntity(world, user, projectileStack, weaponStack) {
                @Override
                protected void onHit(LivingEntity target) {
                    super.onHit(target);
                    this.discard();
					SphereExplosion.explode(world, target.getPos(), 7f, 1.5f, 0.7f, 0.7f, true, user, null);
                }

                @Override
                protected void onBlockHit(BlockHitResult blockHitResult) {
                    super.onBlockHit(blockHitResult);
                    this.discard();
					SphereExplosion.explode(world, blockHitResult.getPos(), 8f, 1.5f, 0.9f, 0.8f, true, user, null);
                }
    }, stack -> 2f, stack -> 2.25f), new Settings()
            .rarity(Rarity.RARE)
            .maxDamage(15)
    );
    public static final Item FALCON_BOW = ModItems.register("falcon_bow", settings -> new BowItem(settings, (world, user, projectileStack,weaponStack) -> new ArrowEntity(world, user, projectileStack, weaponStack) {
                @Override
                protected void onHit(LivingEntity target) {
                    super.onHit(target);
                    this.discard();

                    final Vec3d knockback = this.getVelocity().getHorizontal().add(0d, 0.5d, 0d);
                    if (target instanceof ServerPlayerEntity serverPlayer) {
                        serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer.getId(), serverPlayer.getVelocity().add(knockback)));
                    }
                    target.addVelocity(knockback);
                }
            }, stack -> 0.75f, stack -> 4f), new Settings()
            .rarity(Rarity.EPIC)
            .maxDamage(15)
    );
    public static final Item HEATER_BOW = ModItems.register("heater_bow", settings -> new BowItem(settings, (world, user, projectileStack,weaponStack) -> new ArrowEntity(world, user, projectileStack, weaponStack) {
                @Override
                protected void onHit(LivingEntity target) {
                    super.onHit(target);
                    this.discard();
                    fireExplosion(world, target.getBlockPos(), 5f, 0.125f, user);
                }

                @Override
                protected void onBlockHit(BlockHitResult blockHitResult) {
                    super.onBlockHit(blockHitResult);
                    this.discard();
                    fireExplosion(world, blockHitResult.getBlockPos(), 4f, 0.175f, user);
                }
            }, stack -> 1f, stack -> 3f), new Settings()
            .rarity(Rarity.UNCOMMON)
            .maxDamage(250)
    );
    public static final Item CHORUS_BOW = ModItems.register("chorus_bow", settings -> new BowItem(settings, (world, user, projectileStack,weaponStack) -> new ArrowEntity(world, user, projectileStack, weaponStack) {
                @Override
                protected void onHit(LivingEntity target) {
                    super.onHit(target);
                    this.discard();
                    randomTeleport(world, target, CHORUS_SALAD_TRIES, 7, 1f);
                }

                @Override
                protected void onBlockHit(BlockHitResult blockHitResult) {
                    super.onBlockHit(blockHitResult);
                    randomTeleport(world, this, 2, 15, 2f);
                }
            }, stack -> 0.9f, stack -> 3.5f), new Settings()
            .rarity(Rarity.RARE)
            .maxDamage(75)
    );
    public static final Item CATASTROPHE_BOW = ModItems.register("catastrophe_bow", settings -> new BowItem(settings, (world, user, projectileStack,weaponStack) -> new ArrowEntity(world, user, projectileStack, weaponStack) {
                @Override
                protected void onHit(LivingEntity target) {
                    super.onHit(target);
                    this.discard();
                    if (world instanceof ServerWorld serverWorld) {
                        target.setHealth(0f);
                        //target.damage(serverWorld, damageSource(world, DamageTypes.CATASTROPHE_BOW, user), 69420f);
                    }
                }
            }, stack -> 3f, stack -> 6f) {
                @Override
                public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
                    if (super.rawDrawProgress(stack, remainingUseTicks) > 7.5f) {
						SphereExplosion.explode(world, user.getPos(), 7f, 1.5f, 12f, 3f, true, user, DamageTypes.CATASTROPHE_BOW_BACKFIRE);
                    }
                }
            }, new Settings()
            .rarity(Rarity.EPIC)
            .maxDamage(10)
    );
    public static final Item SEEKING_BOW = ModItems.register("seeking_bow", settings -> new BowItem(settings, (world, user, projectileStack,weaponStack) -> new ArrowEntity(world, user, projectileStack, weaponStack) {
                @Override
                protected void onHit(LivingEntity target) {
                    super.onHit(target);
                    this.discard();

                    GamesManager.getInstance().getGame(user).ifPresent(game -> {
                        if (game instanceof AbstractHidingGameManager<?, ?, ?> hidingGameManager) {
                            hidingGameManager.tryFindPlayer(user, (ServerPlayerEntity)target);
                        }
                    });
                }
            }, stack -> 0.5f, stack -> 3.5f), new Settings()
            .rarity(Rarity.UNCOMMON)
            .maxDamage(500)
            .attributeModifiers(new AttributeModifiersComponent(List.of(
                    new AttributeModifiersComponent.Entry(EntityAttributes.MOVEMENT_SPEED, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_speed"), 0.05d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND, AttributeModifiersComponent.Display.getHidden())
            )))
    );

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!user.getProjectileType(user.getStackInHand(hand)).isEmpty()) {
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player && player.getGameMode() == GameMode.SPECTATOR) return false;

        final float drawProgress = this.drawProgress(stack, remainingUseTicks);
        if (drawProgress < 0.3f && this.rawDrawProgress(stack, remainingUseTicks) < 0.2f) return false;

        final float speed = drawProgress * this.speed.apply(stack);

        List<ItemStack> arrowStack = load(stack, user.getProjectileType(stack), user);
        if (arrowStack.isEmpty()) arrowStack = List.of(Items.ARROW.getDefaultStack());

        if (world instanceof ServerWorld serverWorld) {
            this.shootAll(serverWorld, user, user.getActiveHand(), stack, arrowStack, speed, 1f - drawProgress, drawProgress > 0.95f, null);
        }

        if (user instanceof PlayerEntity player) {
            stack.damage(arrowStack.size(), player);
        }

        playBowSound(world, user, drawProgress);

        return true;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }

    @Override
    public int getRange() {
        return this.speed.apply(this.getDefaultStack()).intValue() * 5;
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        projectile.setVelocity(shooter, shooter.getPitch(), shooter.getYaw() + yaw * 0.25f, 0f, speed, divergence);
    }

    @Override
    protected PersistentProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        final ArrowEntity arrowEntity = this.arrowFactory.build(world, shooter, projectileStack, weaponStack);
        if (critical) {
            arrowEntity.setCritical(true);
        }

        return arrowEntity;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    private float drawProgress(ItemStack stack, int remainingUseTicks) {
        final float getHeldAmount = this.rawDrawProgress(stack, remainingUseTicks);
        final float drawTime = this.drawTime.apply(stack);

        return Math.min(1f, getHeldAmount / drawTime);
    }

    private float rawDrawProgress(ItemStack stack, int remainingUseTicks) {
        return (this.getMaxUseTime(stack, null) - remainingUseTicks + this.useTimeOffset(stack)) / 20f;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        switch (stack.getItem().toString()) {
            case "socwars:catastrophe_bow" -> textConsumer.accept(Text.translatable("tooltip.catastrophe_bow").formatted(Formatting.DARK_RED));
        }
    }

    public static void playBowSound(World world, LivingEntity user, float drawAmount) {
        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS,
                1f,
                1f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + drawAmount * 0.5f
        );
    }

    @Override
    public float getScale(ItemStack stack) {
        return this.drawTime.apply(stack);
    }
}
