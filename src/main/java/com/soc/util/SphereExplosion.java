package com.soc.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class SphereExplosion {
    public static void explode(World world, BlockPos pos, float explosionRadius, ExplosionBehavior behaviour) {
        boolean damage = world instanceof ServerWorld serverWorld && serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES);

        int explosionRadiusInt = (int)Math.ceil(explosionRadius);
        Vec3i cornerSize = new Vec3i(explosionRadiusInt, explosionRadiusInt, explosionRadiusInt);

        Vec3i minPos = pos.subtract(cornerSize);
        Vec3i maxPos = pos.add(cornerSize);

        for (int x = minPos.getX(); x < maxPos.getX(); x++) {
            for (int y = minPos.getY(); y < maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z < maxPos.getZ(); z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    BlockState currentState = world.getBlockState(currentPos);

                    if (!pos.isWithinDistance(new Vec3i(x, y, z), explosionRadius - Random.RANDOM.nextFloat())) continue;
                    if (currentState.isIn(BlockTags.EXPLOSION_IMMUNE)) continue;

                    if (currentState == Blocks.WATER.getDefaultState()) trySpawnSteam(world, x, y, z);

                    if (damage) world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                }
            }
        }

        world.createExplosion(null, Explosion.createDamageSource(world, null), behaviour, pos.getX(), pos.getY() - 2, pos.getZ(), (float)Math.sqrt(explosionRadius), false, World.ExplosionSourceType.TNT);
    }

    private static void trySpawnSteam(World world, float x, float y, float z) {
        float random = world.random.nextFloat();
        if (random < 0.6f) return;
        world.addParticleClient(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0f, random - 0.5f, 0f);
    }
}
