package com.soc.blocks;

import com.mojang.serialization.MapCodec;
import com.soc.blocks.blockentities.CollectibleBlockEntity;
import com.soc.game.manager.GamesManager;
import com.soc.game.map.AbstractGameMap;
import com.soc.player.PlayerDataManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CollectibleBlock extends BlockWithEntity {
    public CollectibleBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(CollectibleBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CollectibleBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CollectibleBlockEntity blockEntity)) {
            return super.onUse(state, world, pos, player, hit);
        }

        if (player.isCreative()) {
            final RegistryEntry<Item> item = player.getStackInHand(Hand.MAIN_HAND).getRegistryEntry();
            final boolean isNewStack = item != Items.AIR && item != blockEntity.getCollectible();

            if (isNewStack) {
                final DisplayEntity.ItemDisplayEntity display = getDisplay(blockEntity, world, pos);
                blockEntity.setCollectible(item);
                if (!world.isClient()) display.setItemStack(item.value().getDefaultStack());
            }
        } else {
            this.collect(player, world, blockEntity);
        }

        return ActionResult.SUCCESS;
    }

    private DisplayEntity.ItemDisplayEntity getDisplay(CollectibleBlockEntity blockEntity, World world, BlockPos pos) {
        if (blockEntity.getDisplayUuid() == null) {
            final DisplayEntity.ItemDisplayEntity display = new DisplayEntity.ItemDisplayEntity(EntityType.ITEM_DISPLAY, world);
            display.setPosition(Vec3d.of(pos).add(0.5f, 2f, 0.5f));
            world.spawnEntity(display);

            blockEntity.setDisplayUuid(display.getUuidAsString());
        }

        return (DisplayEntity.ItemDisplayEntity) world.getEntity(UUID.fromString(blockEntity.getDisplayUuid()));
    }

    private void collect(PlayerEntity player, World world, CollectibleBlockEntity blockEntity) {
        if (blockEntity.getCollectible() == null) return;

        boolean collected = PlayerDataManager.getPlayerData(player).collectCollectible(blockEntity.getCollectible());
        if (world.isClient()) {
            player.sendMessage(Text.translatable(collected ? "collectible.collect" : "collectible.already_collected", blockEntity.getCollectible().value().getName()), false);
        } else if (collected) PlayerDataManager.collectDoubloons(player, 10);
    }
}