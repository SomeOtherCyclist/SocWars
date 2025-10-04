package com.soc.items;

import com.soc.effects.AntiGravity;
import com.soc.effects.Flight;
import com.soc.items.util.ModItems;
import com.soc.items.util.UseFunction;
import com.soc.lib.Coroutine;
import com.soc.lib.Coroutines;
import com.soc.materials.ToolMaterials;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.soc.items.util.ModItems.addItemToGroups;
import static com.soc.lib.SocWarsLib.*;

public class UseFunctionWeapon extends Item {
    private final UseFunction useFunction;

    public UseFunctionWeapon(Settings settings, UseFunction useFunction) {
        super(settings);
        this.useFunction = useFunction;
    }

    public static void initialise() {
        addItemToGroups(DASHREND, ItemGroups.COMBAT);
        addItemToGroups(VELOCITY_STAFF, ItemGroups.TOOLS);
        addItemToGroups(VEXING_STAFF, ItemGroups.COMBAT);
        addItemToGroups(YELLOW_SWORD, ItemGroups.COMBAT);
        addItemToGroups(GRAVITY_ORB, ItemGroups.COMBAT);
        addItemToGroups(GOD_COMPLEX, ItemGroups.COMBAT);
        addItemToGroups(SHRINK_RAY, ItemGroups.COMBAT);
        addItemToGroups(BIGGENING_RAY, ItemGroups.COMBAT);
        addItemToGroups(THE_LINE, ItemGroups.COMBAT);
        addItemToGroups(WHEATENATOR, ItemGroups.COMBAT);
    }

    public static final Item DASHREND = ModItems.register("dashrend", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                float pitchClosenessToHorizontal = 1f - Math.abs(user.getPitch() / 90f);
                float pitchStrength = pitchClosenessToHorizontal * 0.5f + 0.5f;
                float dashStrength = (float) Math.sqrt(pitchStrength) * (user.isOnGround() ? 2f : 0.75f) * 0.5f;

                user.addVelocity(user.getRotationVector().multiply(dashStrength));

                ItemStack item = user.getStackInHand(hand);
                item.damage(Math.round(pitchStrength * 10), user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .sword(ToolMaterials.DASH, 2f, -2f)
            .useCooldown(3.5f)
            .rarity(Rarity.RARE)
    );
    public static final Item VELOCITY_STAFF = ModItems.register("velocity_staff", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                float pitchClosenessToHorizontal = 1f - Math.abs(user.getPitch() / 90f);
                float pitchStrength = pitchClosenessToHorizontal * 0.5f + 0.5f;
                float dashStrength = (float) Math.sqrt(pitchStrength) * (user.isOnGround() ? 2f : 0.75f) * 0.85f;

                user.addVelocity(user.getRotationVector().multiply(dashStrength));

                user.getStackInHand(hand).damage(Math.round(pitchStrength * 10), user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(300)
            .useCooldown(1.2f)
            .rarity(Rarity.RARE)
    );
    public static final Item VEXING_STAFF = ModItems.register("vexing_staff", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                for (int i = 0; i < 2; i++) {
                    VexEntity vex = new VexEntity(EntityType.VEX, world);
                    vex.setPosition(user.getEyePos().add(user.getRotationVector()));

                    Scoreboard scoreboard = world.getScoreboard();
                    if (user.getScoreboardTeam() == null) {
                        user.sendMessage(Text.literal("You are not assigned to a team, go yell at Liam"), false);
                    } else {
                        scoreboard.addScoreHolderToTeam(vex.getUuidAsString(), user.getScoreboardTeam());
                    }

                    world.spawnEntity(vex);

                    user.getStackInHand(hand).damage(1, user, hand);
                }

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(5)
            .useCooldown(45f)
            .rarity(Rarity.RARE)
    );
    public static final Item YELLOW_SWORD = ModItems.register("yellow_sword", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                ItemStack itemStack = user.getStackInHand(hand);

                if (world instanceof ServerWorld serverWorld) {
                    SpectralArrowEntity arrow = ProjectileEntity.spawn(new SpectralArrowEntity(world, user, Items.AIR.getDefaultStack(), itemStack), serverWorld, itemStack);
                    arrow.setPosition(user.getEyePos());
                    arrow.setVelocity(user.getRotationVector().multiply(2f));
                }

                itemStack.damage(3, user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .sword(ToolMaterials.BASE, 6f, -2.1f)
            .useCooldown(1.5f)
            .maxDamage(600)
    );
    public static final Item GRAVITY_ORB = ModItems.register("gravity_orb", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                user.addStatusEffect(new StatusEffectInstance(AntiGravity.ANTI_GRAVITY, (int) 7.5 * 20, 2, false, false));

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(7.5f)
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item GOD_COMPLEX = ModItems.register("god_complex", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                user.addStatusEffect(new StatusEffectInstance(Flight.FLIGHT, 5 * 20, 0, false, false));

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(5f)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .rarity(Rarity.EPIC)
    );
    public static final Item SHRINK_RAY = ModItems.register("shrink_ray", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                shootEntity(user, hand, 1, 10, 2 * 20, entity -> scaleEntity(entity, SQRT2 * 0.5f));

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(16)
            .rarity(Rarity.RARE)
    );
    public static final Item BIGGENING_RAY = ModItems.register("biggening_ray", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                shootEntity(user, hand, 1, 10, 2 * 20, entity -> scaleEntity(entity, SQRT2));

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(16)
            .rarity(Rarity.RARE)
    );
    public static final Item THE_LINE = ModItems.register("the_line", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                if (world.isClient) return ActionResult.SUCCESS;

                final boolean allowsMovementControl = user.isSneaking();
                final Vec3d cachedPosition = new Vec3d(user.getEyePos().toVector3f());

                final AtomicInteger beginContext = new AtomicInteger(7);
                final Vec3d cachedDirection = new Vec3d(user.getRotationVector().toVector3f());

                Coroutines.getInstance().startCoroutine(new Coroutine<>(beginContext, context -> {
                    final int i = context.getAndIncrement();

                    if (i >= 50) return true;
                    final Vec3d pos = (allowsMovementControl ? user.getEyePos() : cachedPosition).add(cachedDirection.multiply(i));

                    final TntEntity tnt = new TntEntity(world, pos.x, pos.y, pos.z, user);
                    tnt.setFuse(20);
                    world.spawnEntity(tnt);

                    return !world.getBlockState(BlockPos.ofFloored(pos)).isAir();
                }));

                user.getStackInHand(hand).decrementUnlessCreative(1, user);

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxCount(4)
            .useCooldown(0f)
            .rarity(Rarity.RARE)
    );
    public static final Item WHEATENATOR = ModItems.register("wheatenator", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                final Vec3d rotationVec = user.getRotationVector();
                final double rotationAngle = Math.atan2(rotationVec.z, rotationVec.x);

                BlockPos[] positions = findAdjacentBlocksFromViewAngle(BlockPos.ofFloored(user.getPos().add(rotationVec.getHorizontal().normalize().multiply(0.5f))), rotationAngle);
                for (int y: new int[] {0, 1}) {
                    for (BlockPos position : positions) {
                        BlockPos currentPos = position.add(0, y, 0);
                        world.setBlockState(currentPos, Blocks.HAY_BLOCK.getDefaultState());
                    }
                }

                user.getStackInHand(hand).decrementUnlessCreative(5, user);

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(350)
            .useCooldown(0.25f)
            .rarity(Rarity.RARE)
    );

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        return useFunction.use(world, user, hand);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        switch (stack.getItem().toString()) {
            case "socwars:knockforward_sword" -> textConsumer.accept(Text.literal("The Hypixel special").formatted(Formatting.GOLD));
            case "socwars:god_complex" -> textConsumer.accept(Text.translatable("tooltip.god_complex"));
            case "socwars:shrink_ray" -> textConsumer.accept(Text.translatable("tooltip.shrink_ray"));
        }
    }

    public static void shootEntity(PlayerEntity user, Hand hand, int missDamage, int hitDamage, int hitCooldown, Consumer<LivingEntity> effect) {
        final EntityHitResult hit = ProjectileUtil.raycast(
                user,
                user.getCameraPosVec(0),
                user.getCameraPosVec(0).add(user.getRotationVector().multiply(250f)),
                user.getBoundingBox().stretch(user.getRotationVector().multiply(250)),
                entity -> entity instanceof LivingEntity,
                250f * 250f
        );

        final ItemStack stack = user.getStackInHand(hand);

        if (hit == null) {
            stack.damage(missDamage, user, hand);
            user.getItemCooldownManager().set(user.getStackInHand(hand), 2);
        } else {
            stack.damage(hitDamage, user, hand);
            user.getItemCooldownManager().set(user.getStackInHand(hand), hitCooldown);

            effect.accept((LivingEntity)hit.getEntity()); //I hereby declare this cast safe because I already filter for living entities
        }
    }
}