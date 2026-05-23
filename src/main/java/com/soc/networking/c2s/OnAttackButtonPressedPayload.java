package com.soc.networking.c2s;

import com.soc.SocWars;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OnAttackButtonPressedPayload(ItemStack stack) implements CustomPayload {
    public static final Identifier ON_ATTACK_BUTTON_PRESSED = Identifier.of(SocWars.MOD_ID, "on_attack_button_pressed");
    public static final Id<OnAttackButtonPressedPayload> ID = new Id<>(ON_ATTACK_BUTTON_PRESSED);
    public static final PacketCodec<RegistryByteBuf, OnAttackButtonPressedPayload> CODEC = PacketCodec.tuple(
            ItemStack.OPTIONAL_PACKET_CODEC, OnAttackButtonPressedPayload::stack,
            OnAttackButtonPressedPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
