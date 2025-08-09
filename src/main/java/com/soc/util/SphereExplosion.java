package com.soc.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class SphereExplosion {
    public static void explode(World world, BlockPos pos, float explosionRadius) {
        int explosionRadiusInt = (int)Math.ceil(explosionRadius);
        Vec3i cornerSize = new Vec3i(explosionRadiusInt, explosionRadiusInt, explosionRadiusInt);

        Vec3i minPos = pos.subtract(cornerSize);
        Vec3i maxPos = pos.add(cornerSize);

        for (int x = minPos.getX(); x < maxPos.getX(); x++) {
            for (int y = minPos.getY(); y < maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z < maxPos.getZ(); z++) {
                    if (pos.isWithinDistance(new Vec3i(x, y, z), explosionRadius - Random.RANDOM.nextFloat())) {
                        world.removeBlock(new BlockPos(x, y, z), false);
                    }
                }
            }
        }

        world.createExplosion(null, Explosion.createDamageSource(world, null), null, pos.getX(), pos.getY() - 2, pos.getZ(), (float)Math.sqrt(explosionRadius), false, World.ExplosionSourceType.TNT);
    }
}
