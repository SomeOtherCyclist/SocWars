package com.soc.blocks;

import com.mojang.serialization.MapCodec;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.networking.s2c.KitBlockSendToScreen;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class KitBlock extends BlockWithEntity {
    public KitBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(KitBlock::new);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof KitBlockEntity blockEntity)) {
            return super.onUse(state, world, pos, player, hit);
        }

        if (player.isCreative()) {
            player.openHandledScreen(blockEntity);
            if (player instanceof ServerPlayerEntity serverPlayer) ServerPlayNetworking.send(serverPlayer, new KitBlockSendToScreen(blockEntity));
        } else {
            if (blockEntity.getAllowedGameTypesList().isEmpty() && world.isClient) { //Yeah I know this is gross but the openKitSelectionScreen function only does something on the server
                player.sendMessage(Text.translatable("message.no_kits"), false);
            } else {
                this.openKitSelectionScreen(blockEntity);
            }
        }

        return ActionResult.SUCCESS;
    }

    private void openKitSelectionScreen(KitBlockEntity blockEntity) {} //Mixin target for client only

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KitBlockEntity(pos, state);
    }
}