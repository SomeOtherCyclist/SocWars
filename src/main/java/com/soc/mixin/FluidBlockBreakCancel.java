package com.soc.mixin;

import com.soc.util.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowableFluid.class)
public abstract class FluidBlockBreakCancel {
    @Inject(at = @At(value = "RETURN"), method = "canFill(Lnet/minecraft/block/BlockState;)Z", cancellable = true)
    private static void socwars_dropItemEvent(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.isIn(ModBlockTags.NO_BREAK_FROM_WATER)) {
            cir.setReturnValue(false);
        };
    }
}