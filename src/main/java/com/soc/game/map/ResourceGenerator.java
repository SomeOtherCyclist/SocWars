package com.soc.game.map;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public class ResourceGenerator {
    protected final World world;
    protected final BlockPos pos;
    protected final ItemStack item;
    protected int generationTime;
    protected int remainingTime;

    public ResourceGenerator(final ItemStack item, final World world, final BlockPos pos) {
        this.world = world;
        this.pos = pos;
        this.item = item;

        generationTime = 30 * 20;
        remainingTime = generationTime;
    }

    public static ImmutableSet<ResourceGenerator> resourceGenerators(final ItemStack item, final World world, final Set<BlockPos> positions) {
        ImmutableSet.Builder<ResourceGenerator> builder = ImmutableSet.builder();
        positions.forEach(pos -> builder.add(new ResourceGenerator(item, world, pos)));
        return builder.build();
    }

    private void generate() {
        ItemEntity entity = new ItemEntity(this.world, this.pos.getX() + 0.5d, this.pos.getY() + 1, this.pos.getZ() + 0.5d, item);
        this.world.spawnEntity(entity);
    }

    public void tick() {
        if (this.remainingTime > 0) {
            this.remainingTime--;
            return;
        }

        this.remainingTime = this.generationTime;

        if (generationTime > 0) this.generate(); //Makes sure that generationTime > 0 so that a gen with generationTime = 0 doesn't constantly spawn items
    }

    public void setStats(GeneratorStats stats) {
        this.generationTime = stats.generationTime();
        item.setCount(stats.count());
    }
}
