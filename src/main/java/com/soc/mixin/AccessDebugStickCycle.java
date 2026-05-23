package com.soc.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.DebugStickItem;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DebugStickItem.class)
public interface AccessDebugStickCycle {
	@Invoker("cycle")
	static <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property, boolean inverse) {
		throw new AssertionError();
	}
}
