package com.soc.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public interface ModCodecs {
    Codec<UUID> UUID = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);
    Codec<Box> BOX = RecordCodecBuilder.create(instance -> instance.group(
            Vec3d.CODEC.fieldOf("min").orElse(Vec3d.ZERO).forGetter(Box::getMinPos),
            Vec3d.CODEC.fieldOf("max").orElse(Vec3d.ZERO).forGetter(Box::getMaxPos)
    ).apply(instance, Box::new));
}
