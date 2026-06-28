package com.soc.lib;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.soc.SocWars;
import com.soc.mixin.GetItemSettingsComponent;
import com.soc.mixin.GetItemSettingsComponentsMap;
import com.soc.mixin.MostRecentDamage;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SocWarsLib {
    public static final DecimalFormat TWO_DIGIT_NUMBER_FORMAT = new DecimalFormat("00");
    public static final Identifier SCALE_MODIFIER_ID = Identifier.of(SocWars.MOD_ID, "scale");
    public static final EquipmentSlot[] ARMOUR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final float SQRT2 = 1.4142135f;
    public static final float MAX_SCALE_FACTOR = 4f;

    public static <T, U> Multimap<T, U> multimapFromCollections(Collection<T> t1, Collection<U> t2) {
        ImmutableMultimap.Builder<T, U> builder = ImmutableMultimap.builder();

        Iterator<T> t1Iterator = t1.iterator();
        Iterator<U> t2Iterator = t2.iterator();

        while (t1Iterator.hasNext() && t2Iterator.hasNext()) {
            builder.put(t1Iterator.next(), t2Iterator.next());
        }

        return builder.build();
    }

    public static <C extends Collection<BlockPos>> Optional<C> getBlockPosCollection(NbtCompound compound, String key, Collector<BlockPos, ?, C> collector) {
        Optional<long[]> value = compound.getLongArray(key);
        return value.map(longs -> Arrays.stream(longs).mapToObj(BlockPos::fromLong).collect(collector));
    }

    public static Optional<Set<BlockPos>> getBlockPosSet(NbtCompound compound, String key) {
        Optional<long[]> value = compound.getLongArray(key);
        return value.map(longs -> Arrays.stream(longs).mapToObj(BlockPos::fromLong).collect(Collectors.toSet()));
    }

    public static void putBlockPosCollection(NbtCompound compound, String key, Collection<BlockPos> blockPosCollection) {
        compound.putLongArray(key, blockPosCollection.stream().mapToLong(BlockPos::asLong).toArray());
    }

    public static Formatting formattingColourFromDye(DyeColor colour) {
        return switch (colour) {
            case WHITE -> Formatting.WHITE;
            case ORANGE -> Formatting.GOLD;
            case MAGENTA, PINK -> Formatting.LIGHT_PURPLE;
            case LIGHT_BLUE -> Formatting.BLUE;
            case YELLOW -> Formatting.YELLOW;
            case LIME -> Formatting.GREEN;
            case GRAY -> Formatting.DARK_GRAY;
            case LIGHT_GRAY -> Formatting.GRAY;
            case CYAN -> Formatting.AQUA;
            case PURPLE -> Formatting.DARK_PURPLE;
            case BLUE -> Formatting.DARK_BLUE;
            case BROWN -> Formatting.DARK_RED;
            case GREEN -> Formatting.DARK_GREEN;
            case RED -> Formatting.RED;
            case BLACK -> Formatting.BLACK;
        };
    }

    public static MutableText colouredTextFromColour(DyeColor colour) {
        return Text.translatable("color.minecraft." + colour.asString()).formatted(formattingColourFromDye(colour));
    }

    public static Item woolItemFromColour(DyeColor colour) {
        return switch (colour) {
            case WHITE -> Items.WHITE_WOOL;
            case ORANGE -> Items.ORANGE_WOOL;
            case MAGENTA -> Items.MAGENTA_WOOL;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_WOOL;
            case YELLOW -> Items.YELLOW_WOOL;
            case LIME -> Items.LIME_WOOL;
            case GRAY -> Items.GRAY_WOOL;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_WOOL;
            case CYAN -> Items.CYAN_WOOL;
            case PURPLE -> Items.PURPLE_WOOL;
            case BLUE -> Items.BLUE_WOOL;
            case BROWN -> Items.BROWN_WOOL;
            case GREEN -> Items.GREEN_WOOL;
            case RED -> Items.RED_WOOL;
            case BLACK -> Items.BLACK_WOOL;
            case PINK -> Items.PINK_WOOL;
        };
    }

    //TODO: Make new armour trim materials
    public static RegistryKey<ArmorTrimMaterial> armourTrimFromColour(DyeColor colour) {
        return switch (colour) {
            case WHITE -> ArmorTrimMaterials.QUARTZ;
            case ORANGE -> ArmorTrimMaterials.RESIN;
            case MAGENTA -> ArmorTrimMaterials.AMETHYST; //Three whole amethyst ones oh baby make I should make a new one
            case LIGHT_BLUE -> ArmorTrimMaterials.DIAMOND; //Maybe make a new one for this instead of cyan? I don't know which one is closer
            case YELLOW -> ArmorTrimMaterials.GOLD;
            case LIME -> ArmorTrimMaterials.EMERALD; //Make a new one
            case GRAY -> ArmorTrimMaterials.IRON; //Make something between iron and netherite
            case LIGHT_GRAY -> ArmorTrimMaterials.IRON;
            case CYAN -> ArmorTrimMaterials.DIAMOND;
            case PURPLE -> ArmorTrimMaterials.AMETHYST;
            case BLUE -> ArmorTrimMaterials.LAPIS;
            case BROWN -> ArmorTrimMaterials.COPPER; //Maybe make a new one since it's close to copper
            case GREEN -> ArmorTrimMaterials.EMERALD;
            case RED -> ArmorTrimMaterials.REDSTONE;
            case BLACK -> ArmorTrimMaterials.NETHERITE;
            case PINK -> ArmorTrimMaterials.AMETHYST; //Make a new one
        };
    }

    public static void scaleEntity(LivingEntity entity, float scale) {
        multiplyAttributeModifier(entity, EntityAttributes.SCALE, SCALE_MODIFIER_ID, scale, MAX_SCALE_FACTOR);
        multiplyAttributeModifier(entity, EntityAttributes.ENTITY_INTERACTION_RANGE, SCALE_MODIFIER_ID, scale, MAX_SCALE_FACTOR);
        multiplyAttributeModifier(entity, EntityAttributes.BLOCK_INTERACTION_RANGE, SCALE_MODIFIER_ID, scale, MAX_SCALE_FACTOR);
    }

    public static void multiplyAttributeModifier(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, Identifier modifierId, float multiplier, float maxMultiplier) {
        final EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        if (instance == null) return;

        final double unclampedModifier = instance.getModifier(modifierId) == null ? multiplier - 1f : ((instance.getModifier(modifierId).value() + 1f) * multiplier - 1f);
        final double finalModifier = Math.clamp(unclampedModifier, (1 - maxMultiplier) / maxMultiplier, maxMultiplier - 1);

        instance.overwritePersistentModifier(new EntityAttributeModifier(
                modifierId,
                finalModifier,
                EntityAttributeModifier.Operation.ADD_VALUE)
        );

        if (Math.abs(instance.getModifier(modifierId).value()) < 1e-5) instance.removeModifier(modifierId);
    }

    public static void resetScale(LivingEntity entity) {
        entity.getAttributeInstance(EntityAttributes.SCALE).removeModifier(SCALE_MODIFIER_ID);
        entity.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE).removeModifier(SCALE_MODIFIER_ID);
        entity.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE).removeModifier(SCALE_MODIFIER_ID);
    }

    public static BlockPos[] findAdjacentBlocksFromViewAngle(BlockPos pos, double angle) {
        return Arrays.stream(new double[] {-Math.PI / 4d, 0, Math.PI / 4d}).mapToObj(offset -> {
            final double a = offset + ((Math.PI / 4d) * Math.round(angle * 4d / Math.PI));
            final double radius = polarSquareRadius(a);

            final int x = (int)Math.round((Math.cos(a) * 100d) / 100d * radius);
            final int z = (int)Math.round((Math.sin(a) * 100d) / 100d * radius);

            return pos.add(x, 0, z);
        }).toArray(BlockPos[]::new);
    }

    public static double polarSquareRadius(double angle) {
        final double piOn2 = (Math.PI * 0.5d);
        final double piOn4 = (Math.PI * 0.25d);

		final double finalAngle = Math.abs((angle - piOn4) % piOn2) - piOn4;

		return 1d / Math.cos(finalAngle);
    }

    public static void iterateInSphere(Vec3i centre, float radius, float randomRadiusFactor, BiConsumer<BlockPos, Double> function) {
        final int intRadius = (int)Math.ceil(radius);
        final Random random = new LocalRandom(centre.getX() + centre.getY() + centre.getZ() + function.hashCode());
        final Predicate<Double> sphereCheckFunction = randomRadiusFactor > 10e-5 ? distance -> distance < radius - random.nextFloat() : distance -> distance < radius;

        iterateInCube(centre, intRadius, pos -> {
            final double distance = Math.sqrt(centre.getSquaredDistance(pos));
            if (sphereCheckFunction.test(distance)) function.accept(pos, distance);
        });
    }

    public static void iterateInSphere(Vec3i centre, float radius, float randomRadiusFactor, Consumer<BlockPos> function) {
        final int intRadius = (int)Math.ceil(radius);
        final Random random = new LocalRandom(centre.getX() + centre.getY() + centre.getZ() + function.hashCode());
        final Predicate<BlockPos> sphereCheckFunction = randomRadiusFactor > 10e-5 ? pos -> centre.isWithinDistance(pos, radius - random.nextFloat()) : pos -> centre.isWithinDistance(pos, radius);

        iterateInCube(centre, intRadius, pos -> {
            if (sphereCheckFunction.test(pos)) function.accept(pos);
        });
    }

    public static void iterateInCube(IntBox box, Consumer<BlockPos> function) {
        iterateInCube(box.getMin(), box.getMax(), function);
    }

    public static void iterateInCube(Vec3i centre, int radius, Consumer<BlockPos> function) {
        final Vec3i cornerSize = new Vec3i(radius, radius, radius);

        final Vec3i minPos = centre.subtract(cornerSize);
        final Vec3i maxPos = centre.add(cornerSize).add(1, 1, 1);

        iterateInCube(minPos, maxPos, function);
    }

    public static void iterateInPlane(Vec3i centre, int radius, Consumer<BlockPos> function) {
        final Vec3i cornerSize = new Vec3i(radius, 0, radius);

        final Vec3i minPos = centre.subtract(cornerSize);
        final Vec3i maxPos = centre.add(cornerSize).add(1, 1, 1);

        iterateInCube(minPos, maxPos, function);
    }

    public static void iterateInCube(Vec3i minPos, Vec3i maxPos, Consumer<BlockPos> function) {
        for (int x = minPos.getX(); x < maxPos.getX(); x++) {
            for (int y = minPos.getY(); y < maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z < maxPos.getZ(); z++) {
                    function.accept(new BlockPos(x, y, z));
                }
            }
        }
    }

    public static boolean isBlockHidden(World world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (world.isAir(pos.offset(direction))) return false;
        }
        return true;
    }

    public static float getHoldTimeSeconds(int progress) {
        return Math.min(1, -progress / 20f);
    }

    public static boolean hasInfinity(ItemStack stack) {
        return stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).getEnchantments().stream().anyMatch(entry -> entry.getKey().isPresent() && entry.getKey().get() == Enchantments.INFINITY);
    }

    public static boolean randomTeleport(World world, Entity user, int attempts, int range, float minRange) {
        Optional<Vec3d> destination = findRandomOpenPos(world, user.getPos(), attempts, range, minRange);

        destination.ifPresent(pos -> user.requestTeleport(pos.x, pos.y, pos.z));

        return destination.isPresent();
    }

    public static Optional<Vec3d> findRandomOpenPos(World world, Position origin, int attempts, int range, float minRange) {
        for (int i = 0; i < attempts; i++) {
            final int candidateX = (int)origin.getX() + world.random.nextBetween(-range, range);
            final int candidateZ = (int)origin.getZ() + world.random.nextBetween(-range, range);

            final int height = world.getTopY(Heightmap.Type.MOTION_BLOCKING, candidateX, candidateZ);
            if (Math.abs(height - origin.getY()) > 10) continue;

            final float dX = candidateX - (float)origin.getX();
            final float dZ = candidateZ - (float)origin.getZ();
            if (dX * dX + dZ * dZ < minRange * minRange) continue;

            return Optional.of(new Vec3d(candidateX, height, candidateZ));
        }
        return Optional.empty();
    }

    public static DamageSource damageSource(World world, RegistryKey<DamageType> damageType, Entity attacker) {
        return new DamageSource(
                world.getRegistryManager()
                        .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                        .getEntry(damageType.getValue()).get(),
                attacker
        );
    }

    public static RegistryEntry<Enchantment> enchantment(World world, RegistryKey<Enchantment> enchantment) {
        return world.getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getEntry(enchantment.getValue()).orElseThrow();
    }

    public static DamageSource damageSource(World world, RegistryKey<DamageType> damageType) {
        return damageSource(world, damageType, null);
    }

    public static void copyTeam(World world, LivingEntity assignee, LivingEntity source) {
        Scoreboard scoreboard = world.getScoreboard();
        if (source.getScoreboardTeam() == null && source instanceof PlayerEntity player) {
            player.sendMessage(Text.literal("You are not assigned to options team, go yell at Liam"), false);
        } else {
            scoreboard.addScoreHolderToTeam(assignee.getNameForScoreboard(), source.getScoreboardTeam());
        }
    }

    public static LivingEntity randomHostileMob(ServerWorld world, Vec3d pos) {
        int index = world.random.nextBetween(0, 19);
        if (index == 20 && world.random.nextFloat() < 0.4f) index = world.random.nextBetween(0, 18); //Hacky way to make the warden options 2% chance

        final LivingEntity mob = switch (index) {
            case 0 -> new ZombieEntity(EntityType.ZOMBIE, world);
            case 1 -> new SkeletonEntity(EntityType.SKELETON, world);
            case 2 -> new CreeperEntity(EntityType.CREEPER, world);
            case 3 -> new SpiderEntity(EntityType.SPIDER, world);
            case 4 -> new GhastEntity(EntityType.GHAST, world);
            case 5 -> new PiglinBruteEntity(EntityType.PIGLIN_BRUTE, world);
            case 6 -> new EndermanEntity(EntityType.ENDERMAN, world);
            case 7 -> new GuardianEntity(EntityType.GUARDIAN, world);
            case 8 -> new PhantomEntity(EntityType.PHANTOM, world);
            case 9 -> {
                final CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
                creeper.onStruckByLightning(world, null);
                yield creeper;
            }
            case 10 -> new WitherSkeletonEntity(EntityType.WITHER_SKELETON, world);
            case 11 -> new BlazeEntity(EntityType.BLAZE, world);
            case 12 -> new IllusionerEntity(EntityType.ILLUSIONER, world);
            case 13 -> new ElderGuardianEntity(EntityType.ELDER_GUARDIAN, world);
            case 14 -> new GiantEntity(EntityType.GIANT, world);
            case 15 -> new EndermiteEntity(EntityType.ENDERMITE, world);
            case 16 -> new SilverfishEntity(EntityType.SILVERFISH, world);
            case 17 -> new HuskEntity(EntityType.HUSK, world);
            case 18 -> new StrayEntity(EntityType.STRAY, world);
            case 19 -> new WardenEntity(EntityType.WARDEN, world);
            default -> null;
        };

        if (mob == null) {
            SocWars.LOGGER.error("Invalid index for random mob: {}", index);
        } else {
            mob.setPosition(pos);
        }

        return mob;
    }

    public static void swapPositions(Entity a, Entity b) {
        final Vec3d aPos = new Vec3d(a.getPos().toVector3f());
        final Vec3d bPos = new Vec3d(b.getPos().toVector3f());

        if (a instanceof PlayerEntity player) {
            player.requestTeleport(bPos.getX(), bPos.getY(), bPos.getZ());
        } else {
            a.setPosition(bPos);
        }

        if (b instanceof PlayerEntity player) {
            player.requestTeleport(aPos.getX(), aPos.getY(), aPos.getZ());
        } else {
            b.setPosition(aPos);
        }
    }

    public static void rainPositions(World world, Vec3d origin, int num, float radius, float minHeight, float maxHeight, Consumer<Vec3d> function) {
        final float heightRange = maxHeight - minHeight;

        for (int i = 0; i < num; i++) {
            final double range = radius * Math.sqrt(world.random.nextFloat());
            final double angle = world.random.nextFloat() * 2d * Math.PI;

            final Vec3d pos = new Vec3d(Math.cos(angle) * range, minHeight + heightRange * world.random.nextFloat(), Math.sin(angle) * range).add(origin);
            function.accept(pos);
        }
    }

    public static <T> T getIndexWithFallback(T[] array, int index, T fallback) {
        return array.length > index ? array[index] : fallback;
    }

    public static int getIndexWithFallback(int[] array, int index, int fallback) {
        return array.length > index ? array[index] : fallback;
    }

    @SafeVarargs
    public static MutableText getTimeFromTicksDynColours(float time, boolean includeTicks, UnaryOperator<Integer>... colours) {
        final int minutes = (int)(time / 60);
        final int seconds = (int)time % 60;

        final MutableText minutesText = Text.literal(TWO_DIGIT_NUMBER_FORMAT.format(minutes)).withColor(getIndexWithFallback(colours, 0, a -> 0xffffffff).apply(minutes));
        final Text secondsText = Text.literal(":" + TWO_DIGIT_NUMBER_FORMAT.format(seconds)).withColor(getIndexWithFallback(colours, 1, a -> 0xffffffff).apply(seconds));

        if (!includeTicks) {
            return minutesText.append(secondsText);
        } else {
            final int ticks = (int)(time * 20) % 20;
            final Text ticksText = Text.literal("+" + TWO_DIGIT_NUMBER_FORMAT.format(ticks)).withColor(getIndexWithFallback(colours, 2, a -> 0xffffffff).apply(ticks));
            return minutesText.append(secondsText).append(ticksText);
        }
    }

    public static MutableText getTimeFromSeconds(float time, boolean includeTicks, int... colours) {
        final int minutes = (int)(time / 60);
        final int seconds = (int)time % 60;

        final MutableText minutesText = Text.literal(TWO_DIGIT_NUMBER_FORMAT.format(minutes)).withColor(getIndexWithFallback(colours, 0, 0xffffffff));
        final Text secondsText = Text.literal(":" + TWO_DIGIT_NUMBER_FORMAT.format(seconds)).withColor(getIndexWithFallback(colours, 1, 0xffffffff));

        if (!includeTicks) {
            return minutesText.append(secondsText);
        } else {
            final int ticks = (int)(time * 20) % 20;
            final Text ticksText = Text.literal("+" + TWO_DIGIT_NUMBER_FORMAT.format(ticks)).withColor(getIndexWithFallback(colours, 2, 0xffffffff));
            return minutesText.append(secondsText).append(ticksText);
        }
    }

    public static Optional<PlayerEntity> getPlayerAttacker(PlayerEntity player) {
        for (DamageRecord record : ((MostRecentDamage)player.getDamageTracker()).getRecentDamage()) {
            final Entity source = record.damageSource().getSource();
            if (source != null && source.getType() == EntityType.PLAYER) {
                return Optional.of((PlayerEntity)source);
            }
        }
        return Optional.empty();
    }

    public static String romanNumerals(int input) {
        //Rewrite this in a procedural and not stupid way
        return switch(input) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> "";
        };
    }

    public static MutableText romanNumeralsText(int i) {
        return Text.translatable("enchantment.level." + i);
    }

    public static boolean inventoryCanAcceptStack(Inventory inventory, ItemStack insertStack) {
        final Iterator<ItemStack> iter = inventory.iterator();

        final int maxCount = insertStack.getMaxCount();
        int remainingCount = insertStack.getCount();

        for (int i = 0; iter.hasNext(); i++) {
            final ItemStack inventoryStack = iter.next();
            if (inventoryStack.isEmpty() && i < PlayerInventory.MAIN_SIZE) return true;

            if (ItemStack.areItemsAndComponentsEqual(insertStack, inventoryStack)) {
                remainingCount -= maxCount - inventoryStack.getCount();

                if (remainingCount <= 0) return true;
            }
        }

        return false;
    }

    /*
    public static boolean inventoryCanAcceptStack(Inventory inventory, ItemStack insertStack) {
        final Iterator<ItemStack> iter = inventory.iterator();

        final int maxCount = insertStack.getMaxCount();
        int remainingCount = insertStack.getCount();

        for (int i = 0; iter.hasNext(); i++) {
            final ItemStack inventoryStack = iter.next();
            if (inventoryStack.isEmpty()) return true;

            if (ItemStack.areItemsAndComponentsEqual(insertStack, inventoryStack)) {
                remainingCount -= maxCount - inventoryStack.getCount();
            }
        }

        return remainingCount == 0;
    }
     */

    public static void depositStackIntoInventory(ItemStack stack, Inventory inventory, PlayerEntity player, boolean allMatching) {
        if (inventory == null) return;

        final Text depositedName = stack.toHoverableText();
        final Item depositedItem = stack.getItem();
        final AtomicInteger depositedCount = new AtomicInteger();

        if (allMatching) {
            player.getInventory().forEach(eachStack -> {
                if (eachStack.isOf(depositedItem)) {
                    internalDepositStackIntoInventory(eachStack, inventory, depositedCount);
                }
            });
        } else {
            internalDepositStackIntoInventory(stack, inventory, depositedCount);
        }

        inventory.markDirty();

        if (depositedCount.get() > 0) {
            player.sendMessage(Text.translatable("game.chest.deposit", depositedCount.get(), depositedName, Text.translatable("game.chest.count", inventory.count(depositedItem)).formatted(Formatting.GRAY)).formatted(Formatting.DARK_GREEN), false);
        }
    }

    private static void internalDepositStackIntoInventory(ItemStack stack, Inventory inventory, AtomicInteger deposited) {
        for (int i = 0; i < inventory.size() && stack.getCount() > 0; i++) {
            final ItemStack slot = inventory.getStack(i);

            if ((slot.isEmpty() || ItemStack.areItemsAndComponentsEqual(slot, stack))) {
                final int capacity = Math.min(stack.getMaxCount() - slot.getCount(), stack.getCount());
                inventory.setStack(i, stack.copyWithCount(slot.getCount() + capacity));
                stack.setCount(stack.getCount() - capacity);

                deposited.getAndAdd(capacity);
            }
        }
    }

    public static Vec3d randomCentredVec3d(Random random, double xSize, double ySize, double zSize) {
        return new Vec3d(random.nextDouble() * 2 * xSize - xSize, random.nextDouble() * 2 * ySize - ySize, random.nextDouble() * 2 * zSize - zSize);
    }

    public static Vec3d randomCentredVec3d(Random random, double size) {
        return randomCentredVec3d(random, size, size, size);
    }

    public static Vec3d randomCentredVec3d(Random random) {
        return randomCentredVec3d(random, 1d);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getComponentFromSettingsBuilder(Item.Settings settings, ComponentType<T> component) {
        final ComponentMap.Builder builder = ((GetItemSettingsComponentsMap)settings).getComponents();
        return (T)((GetItemSettingsComponent)builder).getComponents().get(component);
    }

    public static <T> void enumerate(Iterable<T> iterable, BiConsumer<Integer, T> function) {
        int i = 0;
        for (T t : iterable) {
            function.accept(i++, t);
        }
    }

    public static <T> void enumerate(T[] array, BiConsumer<Integer, T> function) {
        for (int i = 0; i < array.length; i++) {
            function.accept(i, array[i]);
        }
    }

    public static <K, V> void enumerateMap(Map<K, V> map, TriConsumer<Integer, K, V> function) {
        final AtomicInteger i = new AtomicInteger(0);
        map.forEach((key, value) -> function.accept(i.getAndIncrement(), key, value));
    }

    public static double sqrDistanceToUnitVector(Vec3d origin, Vec3d unitDirection, Vec3d point) {
        final Vec3d rebasedPoint = point.subtract(origin);
        final double sqrPointMagnitude = rebasedPoint.lengthSquared();
        final double pointDotOrigin = rebasedPoint.dotProduct(unitDirection);

        return sqrPointMagnitude * (1d - pointDotOrigin * pointDotOrigin / sqrPointMagnitude);
    }

    public static boolean isPointWithinDistanceOfUnitVector(Vec3d origin, Vec3d unitDirection, Vec3d point, double maxDistance) {
        return sqrDistanceToUnitVector(origin, unitDirection, point) <= maxDistance * maxDistance;
    }

    public static double sqrDistanceToVector(Vec3d origin, Vec3d direction, Vec3d point) {
        return sqrDistanceToUnitVector(origin, direction.normalize(), point);
    }

    public static double distanceToUnitVector(Vec3d origin, Vec3d direction, Vec3d point) {
        return Math.sqrt(sqrDistanceToUnitVector(origin, direction, point));
    }

    public static double distanceToVector(Vec3d origin, Vec3d direction, Vec3d point) {
        return Math.sqrt(sqrDistanceToVector(origin, direction, point));
    }

    public static <T> void ifNotNull(@Nullable T o, Consumer<T> f) {
        if (o != null) f.accept(o);
    }

    public static <T> void ifNotNullElse(@Nullable T o, Consumer<T> f, Runnable r) {
        if (o != null) {
            f.accept(o);
        } else {
            r.run();
        }
    }

    public static <T, U> U mapIfNotNull(@Nullable T o, Function<T, U> mapper, U def) {
        if (o != null) return mapper.apply(o);
        return def;
    }

    public static <T, U> U propogateNull(@Nullable T o, Function<T, U> mapper) {
        return mapIfNotNull(o, mapper, null);
    }

    public static int max(int... ints) {
        int max = Integer.MIN_VALUE;
        for (int i : ints) {
            if (i > max) max = i;
        }

        return max;
    }

    public static <T> List<T> mapFromRange(int start, int end, IntFunction<T> mapper) {
        final List<T> list = new ArrayList<>(end - start);
        for (int i = start; i < end; i++) {
            list.add(mapper.apply(i));
        }

        return list;
    }

    public static @NotNull Item leatherArmour(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> Items.LEATHER_HELMET;
            case CHEST -> Items.LEATHER_CHESTPLATE;
            case LEGS -> Items.LEATHER_LEGGINGS;
            case FEET -> Items.LEATHER_BOOTS;
            default -> throw new IllegalArgumentException("No such leather armour exists for slot " + slot.getName()); //Unreachable
        };
    }

    public static RegistryEntry<Enchantment> enchantmentEntry(World world, RegistryKey<Enchantment> enchantmentKey) {
        return world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(enchantmentKey);
    }

    public static <T, K, V> Map<K, V> mapFromArray(T[] array, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return Arrays.stream(array).collect(Collectors.toMap(keyMapper, valueMapper));
    }

    public static <K, V> Map<K, V> mapFromArray(K[] array, Function<K, V> valueMapper) {
        return mapFromArray(array, Function.identity(), valueMapper);
    }

    public static <K, V> Map<K, V> mapFromArrayEnumerate(K[] array, BiFunction<Integer, K, V> valueMapper) {
        final AtomicInteger i = new AtomicInteger(0);
        return Arrays.stream(array).collect(Collectors.toMap(Function.identity(), key -> valueMapper.apply(i.getAndIncrement(), key), (a, b) -> a, LinkedHashMap::new));
    }

    public static <I, O> Stream<O> mapEnumerate(Stream<I> collection, BiFunction<Integer, I, O> mapper) {
        final AtomicInteger i = new AtomicInteger(0);
        return collection.map(element -> mapper.apply(i.getAndIncrement(), element));
    }

    public static <I, O> Stream<O> mapEnumerate(Collection<I> collection, BiFunction<Integer, I, O> mapper) {
        return mapEnumerate(collection.stream(), mapper);
    }

    public static <I, O> Stream<O> mapEnumerate(I[] array, BiFunction<Integer, I, O> mapper) {
        return mapEnumerate(Arrays.stream(array), mapper);
    }

    public static Vec3d round(Vec3d vector) {
        return new Vec3d(Math.round(vector.x), Math.round(vector.y), Math.round(vector.z));
    }

    public static Vec3d floor(Vec3d vector) {
        return new Vec3d(Math.floor(vector.x), Math.floor(vector.y), Math.floor(vector.z));
    }

    public static Vec3d floorXZ(Vec3d vector) {
        return new Vec3d(Math.floor(vector.x), vector.y, Math.floor(vector.z));
    }

    public static Vec3d modulo(Vec3d vector, double modulus) {
        return new Vec3d(vector.x % modulus, vector.y % modulus, vector.z % modulus);
    }

    public static <T> T getRandomElement(List<T> list, Random random) {
        return list.get(random.nextBetween(0, list.size() - 1));
    }
}