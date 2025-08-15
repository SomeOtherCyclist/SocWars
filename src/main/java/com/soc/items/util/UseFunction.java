package com.soc.items.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface UseFunction {
    ActionResult use(World world, PlayerEntity user, Hand hand);
}
