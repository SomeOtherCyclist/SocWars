package com.soc;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.LongStream;

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

    public static void putBlockPosSet(NbtCompound compound, String key, Collection<BlockPos> blockPosCollection) {
        long[] values = new long[blockPosCollection.size()];
        List<BlockPos> blockPosList = blockPosCollection.stream().toList();

        for (int i = 0; i < blockPosCollection.size(); i++) {
            values[i] = blockPosList.get(i).asLong();
        }

        compound.putLongArray(key, values);
    }
}
