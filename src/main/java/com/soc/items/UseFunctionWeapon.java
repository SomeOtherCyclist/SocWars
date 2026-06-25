package com.soc.items;

import com.soc.effects.util.ModEffects;
import com.soc.entities.BlueShellEntity;
import com.soc.entities.RedShellEntity;
import com.soc.items.util.ModItems;
import com.soc.items.util.UseFunction;
import com.soc.lib.Coroutine;
import com.soc.lib.Coroutines;
import com.soc.lib.IntBox;
import com.soc.materials.ToolMaterials;
import com.soc.networking.s2c.BatchParticlePayload;
import com.soc.util.DamageTypes;
import com.soc.util.Sounds;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static com.soc.items.BowItem.playBowSound;
import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static com.soc.lib.SocWarsLib.*;
import static com.soc.game.manager.AbstractGameManager.getBlockDamagePredicate;

public class UseFunctionWeapon extends Item {
    private static final SoundEvent[] TAUNTS = {
            SoundEvents.BLOCK_ANVIL_USE,
            SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER,
            SoundEvents.BLOCK_BELL_USE,
            SoundEvents.ENTITY_FROG_DEATH,
            SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(),
            SoundEvents.ENTITY_VILLAGER_YES,
            SoundEvents.ENTITY_VILLAGER_AMBIENT,
            SoundEvents.ENTITY_CHICKEN_HURT,
            SoundEvents.ENTITY_FOX_SCREECH,
            SoundEvents.ENTITY_COD_FLOP,
            SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
            SoundEvents.ENTITY_GOAT_SCREAMING_AMBIENT,
            SoundEvents.ENTITY_GOAT_SCREAMING_DEATH,
            SoundEvents.ENTITY_GOAT_SCREAMING_PREPARE_RAM
    };

    private final UseFunction useFunction;

    public UseFunctionWeapon(Settings settings, UseFunction useFunction) {
        super(settings);
        this.useFunction = useFunction;
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(DASHREND, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(VELOCITY_STAFF, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(VEXING_STAFF, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(YELLOW_SWORD, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(GRAVITY_ORB, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(GOD_COMPLEX, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(SCROLL_OF_EAU, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(SCROLL_OF_HELLFIRE, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(C_U_E_B, ItemGroups.TOOLS);
        addItemToGroupsAndBaseItemGroup(SHRINK_RAY, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(BIGGENING_RAY, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(THE_LINE, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(WHEATENATOR, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(DEATH_RAIN, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(ALPHA_BOW, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(SNIPER_RIFLE, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(RED_SHELL, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(BLUE_SHELL, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(TAUNT_STICK, ItemGroups.COMBAT);
    }

    public static final Item DASHREND = ModItems.register("dashrend", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                final float pitchClosenessToHorizontal = 1f - Math.abs(user.getPitch() / 90f);
                final float pitchStrength = pitchClosenessToHorizontal * 0.5f + 0.5f;
                final float dashStrength = (float) Math.sqrt(pitchStrength) * (user.isOnGround() ? 2f : 0.75f) * 0.5f;

                user.addVelocity(user.getRotationVector().multiply(dashStrength));

		        user.getStackInHand(hand).damage(Math.round(pitchStrength * 10), user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .sword(ToolMaterials.DASH, 2f, -2f)
            .useCooldown(3.5f)
            .rarity(Rarity.RARE)
    );
    public static final Item VELOCITY_STAFF = ModItems.register("velocity_staff", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                final float pitchClosenessToHorizontal = 1f - Math.abs(user.getPitch() / 90f);
                final float pitchStrength = pitchClosenessToHorizontal * 0.5f + 0.5f;
                final float dashStrength = (float) Math.sqrt(pitchStrength) * (user.isOnGround() ? 2f : 0.75f) * 1.5f;

                user.addVelocity(user.getRotationVector().multiply(dashStrength));

                user.getStackInHand(hand).damage(Math.round(pitchStrength * 10), user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(300)
            .useCooldown(2f)
            .rarity(Rarity.RARE)
    );
    public static final Item VEXING_STAFF = ModItems.register("vexing_staff", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                for (int i = 0; i < 2; i++) {
                    VexEntity vex = new VexEntity(EntityType.VEX, world);
                    vex.setPosition(user.getEyePos().add(user.getRotationVector()));

                    copyTeam(world, vex, user);

                    world.spawnEntity(vex);

                    user.getStackInHand(hand).damage(1, user, hand);
                }

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(5)
            .useCooldown(45f)
            .rarity(Rarity.RARE)
    );
    public static final Item YELLOW_SWORD = ModItems.register("yellow_sword", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                final ItemStack itemStack = user.getStackInHand(hand);

                if (world instanceof ServerWorld serverWorld) {
                    SpectralArrowEntity arrow = ProjectileEntity.spawn(new SpectralArrowEntity(world, user, Items.AIR.getDefaultStack(), itemStack), serverWorld, itemStack);
                    arrow.setPosition(user.getEyePos());
                    arrow.setVelocity(user.getRotationVector().multiply(2f));
                    arrow.setPitch(user.getPitch());
                    arrow.setYaw(user.getYaw());
                }

                itemStack.damage(3, user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .sword(ToolMaterials.BASE, 6f, -2.1f)
            .useCooldown(1.5f)
            .maxDamage(600)
    );
    public static final Item GRAVITY_ORB = ModItems.register("gravity_orb", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                user.addStatusEffect(new StatusEffectInstance(ModEffects.ANTI_GRAVITY, (int) 7.5 * 20, 2, false, false));
                user.getStackInHand(hand).decrementUnlessCreative(1, user);

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(7.5f)
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item GOD_COMPLEX = ModItems.register("god_complex", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                user.addStatusEffect(new StatusEffectInstance(ModEffects.FLIGHT, 5 * 20, 0, false, false));
                user.getStackInHand(hand).decrementUnlessCreative(1, user);

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(5f)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .rarity(Rarity.EPIC)
    );
    public static final Item SCROLL_OF_EAU = ModItems.register("scroll_of_eau", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                final BlockHitResult hit = world.raycast(new RaycastContext(user.getEyePos(), user.getEyePos().add(user.getRotationVector().multiply(25f)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, user));
                final ItemStack stack = user.getStackInHand(hand);

                if (hit != null && !world.isAir(hit.getBlockPos())) {
                    iterateInSphere(hit.getBlockPos(), 4.5f, 0f, pos -> {
                        if (world.isAir(pos)) world.setBlockState(pos, Blocks.WATER.getDefaultState().with(Properties.LEVEL_15, 7));
                    });
                    world.setBlockState(hit.getBlockPos().add(0,4,0), Blocks.WATER.getDefaultState());
                    stack.damage(3, user, hand);
                }

                stack.damage(1, user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(1f)
            .maxDamage(3)
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item SCROLL_OF_HELLFIRE = ModItems.register("scroll_of_hellfire", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                final BlockHitResult hit = world.raycast(new RaycastContext(user.getEyePos(), user.getEyePos().add(user.getRotationVector().multiply(25f)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, user));
                final ItemStack stack = user.getStackInHand(hand);

                if (hit != null && !world.isAir(hit.getBlockPos())) {
                    iterateInSphere(hit.getBlockPos(), 4.5f, 0f, pos -> {
                        if (world.isAir(pos)) world.setBlockState(pos, Blocks.LAVA.getDefaultState());
                    });
                    stack.damage(3, user, hand);
                }

                stack.damage(1, user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(1f)
            .maxDamage(3)
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item C_U_E_B = ModItems.register("c_u_e_b", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                final BlockPos centre = BlockPos.ofFloored(user.getEyePos().add(user.getRotationVector().multiply(20f)));

                iterateInCube(centre, 5, pos -> {
                    if (world.isAir(pos)) world.setBlockState(pos, Blocks.IRON_BLOCK.getDefaultState());
                });

                world.playSound(null, centre, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS);

                user.getStackInHand(hand).decrementUnlessCreative(1, user);

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(1f)
    );
    public static final Item SHRINK_RAY = ModItems.register("shrink_ray", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                shootEntity(user, hand, 1, 10, 2 * 20, entity -> scaleEntity(entity, SQRT2 * 0.5f));

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(16)
            .rarity(Rarity.RARE)
    );
    public static final Item BIGGENING_RAY = ModItems.register("biggening_ray", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                shootEntity(user, hand, 1, 10, 2 * 20, entity -> scaleEntity(entity, SQRT2));

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(16)
            .rarity(Rarity.RARE)
    );
    public static final Item THE_LINE = ModItems.register("the_line", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
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
            .useCooldown(2f)
            .rarity(Rarity.RARE)
    );
    public static final Item WHEATENATOR = ModItems.register("wheatenator", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                final Vec3d rotationVec = user.getRotationVector();
                final double rotationAngle = Math.atan2(rotationVec.z, rotationVec.x);

                final BiPredicate<BlockPos, BlockState> blockDamagePredicate = getBlockDamagePredicate(world, true, user);

                final BlockPos[] positions = findAdjacentBlocksFromViewAngle(BlockPos.ofFloored(user.getPos().add(rotationVec.getHorizontal().normalize().multiply(0.5f))), rotationAngle);
                for (int y: new int[] {0, 1}) {
                    for (BlockPos position : positions) {
                        final BlockPos pos = position.up(y);
                        if (blockDamagePredicate.test(pos, world.getBlockState(pos))) {
                            world.setBlockState(pos, Blocks.HAY_BLOCK.getDefaultState());
                        }
                    }
                }

                user.getStackInHand(hand).damage(5, user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .sword(ToolMaterials.BASE, 4.5f, -2.4f)
            .maxDamage(350)
            .useCooldown(0.25f)
    );
    public static final Item DEATH_RAIN = ModItems.register("death_rain", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                rainPositions(world, user.getPos(), 15, 15f, 20f, 35f, pos -> {
                    final TntEntity tnt = new TntEntity(world, pos.x, pos.y, pos.z, user);

                    tnt.setFuse(world.random.nextBetween(45, 75));
                    world.spawnEntity(tnt);
                });

                user.getStackInHand(hand).damage(1, user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(3)
            .rarity(Rarity.RARE)
    );
    public static final Item ALPHA_BOW = ModItems.register("alpha_bow", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                final ItemStack itemStack = user.getStackInHand(hand);

                if (world instanceof ServerWorld serverWorld) {
                    final ArrowEntity arrow = new ArrowEntity(world, user, Items.AIR.getDefaultStack(), itemStack);
                    arrow.setPosition(user.getEyePos());
                    arrow.setVelocity(user.getRotationVector().multiply(1.75f));
                    arrow.setPitch(-user.getPitch());
                    arrow.setYaw(-user.getYaw());

                    ProjectileEntity.spawn(arrow, serverWorld, itemStack);
                }

                playBowSound(world, user, 1f);

                itemStack.damage(1, user, hand);

                return ActionResult.FAIL;
            }), new Settings()
            .maxDamage(256)
            .enchantable(2)
            .rarity(Rarity.RARE)
    );
    public static final Item SNIPER_RIFLE = ModItems.register("sniper_rifle", settings -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                final ItemStack itemStack = user.getStackInHand(hand);
                itemStack.damage(1, user, hand);
                world.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.SNIPER_RIFLE_SHOT, SoundCategory.PLAYERS);

                //block damage and particles
                final Set<Vec3d> particlePositions = new HashSet<>();
                final BiPredicate<BlockPos, BlockState> damage = getBlockDamagePredicate(world, true, user);

                //actually technical stuff setup
                final double maxDistance = 30d;
                final double width = 1.5f;
                final double boxStep = 8d;

                final Vec3d eyePos = user.getEyePos();
                final Vec3d direction = user.getRotationVector();

                for (double distance = 0; distance < maxDistance;) {
                    final Box checkBox = new Box(eyePos.add(direction.multiply(distance)), eyePos.add(direction.multiply(distance += boxStep))).expand(Math.ceil(width));

                    iterateInCube(new IntBox(checkBox), pos -> {
                        if (isPointWithinDistanceOfUnitVector(eyePos, direction, pos.toCenterPos(), width) && !world.isAir(pos)) {
                            if (damage.test(pos, world.getBlockState(pos))) world.setBlockState(pos, Blocks.AIR.getDefaultState());
                            if (world.random.nextFloat() < 0.3f) particlePositions.add(pos.toCenterPos());
                        }
                    });
                }

                if (world instanceof ServerWorld serverWorld) {
                    final Collection<ServerPlayerEntity> nearbyPlayers = PlayerLookup.around(serverWorld, eyePos, 250d);

                    //particles 2: electric boogaloo
                    final Vec3d velocity = direction.multiply(-0.05d).add(0d, 0.025d, 0d);
                    final Vec3d mainParticePosition = eyePos.add(direction);
                    nearbyPlayers.forEach(player -> {
                        ServerPlayNetworking.send(player, new BatchParticlePayload(ParticleTypes.LARGE_SMOKE, particlePositions.stream().toList(), velocity));
                        player.networkHandler.sendPacket(new ParticleS2CPacket(ParticleTypes.FLAME, true, true, mainParticePosition.x, mainParticePosition.y, mainParticePosition.z, 0.07f, 0.07f, 0.07f, 0.1f, 16));
                    });

                    //entity damage
                    final List<Entity> entities = new ArrayList<>();
                    EntityHitResult hitResult;
                    do {
                        hitResult = ProjectileUtil.raycast(user, eyePos, eyePos.add(direction.multiply(maxDistance)), new Box(eyePos, eyePos.add(direction.multiply(maxDistance))), entity -> !entities.contains(entity), maxDistance * maxDistance);
                        if (hitResult != null) {
                            entities.add(hitResult.getEntity());
                        }
                    } while (hitResult != null && hitResult.getEntity().squaredDistanceTo(user.getPos()) <= maxDistance * maxDistance);

                    entities.forEach(entity -> entity.damage(serverWorld, damageSource(serverWorld, DamageTypes.SNIPER_RIFLE, user), 18f));
                }

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(10)
            .useCooldown(2f)
            .rarity(Rarity.EPIC)
    );
    public static final Item RED_SHELL = ModItems.register("red_shell", settings -> new UseFunctionWeapon(settings, (world, player, hand) -> {
                if (world.isClient) return null;

                world.spawnEntity(new RedShellEntity(world, player.getPos(), player));
                player.getStackInHand(hand).decrementUnlessCreative(1, player);

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(5f)
            .rarity(Rarity.RARE)
    );
    public static final Item BLUE_SHELL = ModItems.register("blue_shell", settings -> new UseFunctionWeapon(settings, (world, player, hand) -> {
                if (world.isClient) return null;

                world.spawnEntity(new BlueShellEntity(world, player.getPos(), player));
                player.getStackInHand(hand).decrementUnlessCreative(1, player);

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(5f)
            .rarity(Rarity.RARE)
    );
    public static final Item TAUNT_STICK = ModItems.register("taunt_stick", settings -> new UseFunctionWeapon(settings, (world, player, hand) -> {
                final SoundEvent soundEvent = TAUNTS[world.random.nextBetween(0, TAUNTS.length - 1)];
                world.playSound(null, player.getBlockPos(), soundEvent, SoundCategory.MASTER);

                return ActionResult.SUCCESS;
            }), new Settings()
            .rarity(Rarity.UNCOMMON)
            .useCooldown(5f)
    );

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        return this.useFunction.use(world, user, hand);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        switch (stack.getItem().toString()) {
            case "socwars:god_complex" -> textConsumer.accept(Text.translatable("tooltip.god_complex"));
            case "socwars:shrink_ray" -> textConsumer.accept(Text.translatable("tooltip.shrink_ray"));
            case "socwars:scroll_of_eau" -> textConsumer.accept(Text.translatable("tooltip.scroll_of_eau"));
            case "socwars:scroll_of_hellfire" -> textConsumer.accept(Text.translatable("tooltip.scroll_of_hellfire"));
            case "socwars:wheatenator" -> textConsumer.accept(Text.translatable("tooltip.wheatenator"));
        }
    }

    public static void shootEntity(PlayerEntity user, Hand hand, int missDamage, int hitDamage, int hitCooldown, Consumer<LivingEntity> effect) {
        final EntityHitResult hit = ProjectileUtil.raycast(
                user,
                user.getEyePos(),
                user.getEyePos().add(user.getRotationVector().multiply(250d)),
                user.getBoundingBox().stretch(user.getRotationVector().multiply(250d)),
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