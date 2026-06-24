package com.soc.mixin;

import com.soc.entities.util.AllowsDismount;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class DisableEntityDismount extends Entity {
    public DisableEntityDismount(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "tickRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasVehicle()Z"))
    private boolean socwars_disableEntityDismount(PlayerEntity instance) {
        return instance.hasVehicle() && (!instance.getCommandTags().contains("disallow_leave") && (!(this.getVehicle() instanceof AllowsDismount allowsDismountEntity) || allowsDismountEntity.allowsDismount()));
    }
}
