package com.soc.lib;

import com.google.common.collect.ImmutableMap;
import com.soc.SocWars;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class SocWarsLib {
    public static final Identifier SCALE_MODIFIER_ID = Identifier.of(SocWars.MOD_ID, "scale");
    public static final float SQRT2 = (float)Math.sqrt(2d);
    public static final float MAX_SCALE_FACTOR = 4f;
    public static final byte BLOCKPOS_NBT_TYPE = 101;

    public static <T, U> ImmutableMap<T, U> mapFromCollections(Collection<T> t1, Collection<U> t2) {
        ImmutableMap.Builder<T, U> builder = ImmutableMap.builder();

        Iterator<T> t1Iterator = t1.iterator();
        Iterator<U> t2Iterator = t2.iterator();

        while (t1Iterator.hasNext() && t2Iterator.hasNext()) {
            builder.put(t1Iterator.next(), t2Iterator.next());
        }

        return builder.build();
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

    public static DyeColor dyeColourFromOrdinal(int ordinal) {
        final DyeColor[] values = DyeColor.values();
        return values[ordinal < values.length ? ordinal : 0];
    }

    public static <T, U> List<T> collectionPairToLeftList(Collection<Pair<T, U>> collection) {
        return collection.stream().map(Pair::getLeft).toList();
    }

    public static <T, U> List<U> collectionPairToRightList(Collection<Pair<T, U>> collection) {
        return collection.stream().map(Pair::getRight).toList();
    }

    public static void scaleEntity(LivingEntity entity, float scale) {
        final EntityAttributeInstance scaleInstance = entity.getAttributeInstance(EntityAttributes.SCALE);

        scaleInstance.overwritePersistentModifier(new EntityAttributeModifier(
                SCALE_MODIFIER_ID,
                Math.clamp(MAX_SCALE_FACTOR - 1, (1 - MAX_SCALE_FACTOR) / MAX_SCALE_FACTOR, scaleInstance.getModifier(SCALE_MODIFIER_ID) == null ? scale - 1f : (scaleInstance.getModifier(SCALE_MODIFIER_ID).value() + (scale - 1f) / scale) * scale),
                EntityAttributeModifier.Operation.ADD_VALUE)
        );

        if (Math.abs(scaleInstance.getModifier(SCALE_MODIFIER_ID).value()) < 1e-5) scaleInstance.removeModifier(SCALE_MODIFIER_ID);
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
        final double pi2 = (Math.PI / 2d);
        final double pi4 = (Math.PI / 4d);

        final double angleMinusPi4 = angle - pi4;
        final double angleModPi2 = Math.abs(angleMinusPi4 % pi2);
        final double finalAngle = angleModPi2 - pi4;

        final double cosAngle = Math.cos(finalAngle);
        final double result = 1d / cosAngle;

        return result;
    }
}
