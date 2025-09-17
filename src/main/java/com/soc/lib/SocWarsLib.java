package com.soc.lib;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class SocWarsLib {
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
        return value.map(longs -> Set.copyOf(Arrays.stream(longs).mapToObj(BlockPos::fromLong).toList()));
    }

    public static void putBlockPosCollection(NbtCompound compound, String key, Collection<BlockPos> blockPosCollection) {
        long[] values = new long[blockPosCollection.size()];
        List<BlockPos> blockPosList = blockPosCollection.stream().toList();

        for (int i = 0; i < blockPosCollection.size(); i++) {
            values[i] = blockPosList.get(i).asLong();
        }

        compound.putLongArray(key, values);
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
        ArrayList<T> list = new ArrayList<>();
        collection.forEach(pair -> list.add(pair.getLeft()));
        return list;
    }

    public static <T, U> List<U> collectionPairToRightList(Collection<Pair<T, U>> collection) {
        ArrayList<U> list = new ArrayList<>();
        collection.forEach(pair -> list.add(pair.getRight()));
        return list;
    }
}
