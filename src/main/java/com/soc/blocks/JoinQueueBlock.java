package com.soc.blocks;

import com.mojang.serialization.MapCodec;
import com.soc.game.manager.GameType;
import com.soc.game.manager.GamesManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class JoinQueueBlock extends Block {
    public static final MapCodec<JoinQueueBlock> CODEC = createCodec(JoinQueueBlock::new);
    public static final EnumProperty<GameType> QUEUE = EnumProperty.of("queue_type", GameType.class);
    private static final GameType[] QUEUE_TYPES = GameType.values();

    @Override
    public MapCodec<? extends JoinQueueBlock> getCodec() {
        return CODEC;
    }

    public JoinQueueBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(QUEUE, GameType.SKYWARS));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(QUEUE);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.isCreativeLevelTwoOp() && player.isSneaking()) {
            GameType newQueue = QUEUE_TYPES[(state.get(QUEUE).ordinal() + 1) % QUEUE_TYPES.length];
            world.setBlockState(pos, state.with(QUEUE, newQueue));
            if (world.isClient) player.sendMessage(Text.translatable("queue_block.set_queue", newQueue), false);
            return ActionResult.SUCCESS;
        }

        if (world.isClient) return ActionResult.SUCCESS;

        GameType queue = world.getBlockState(pos).get(QUEUE);

        if (!player.isSneaking()) {
            GamesManager.getInstance().queuePlayer((ServerPlayerEntity)player, queue);
        } else {
            GamesManager.getInstance().unqueuePlayer((ServerPlayerEntity)player, queue);
        }

        return ActionResult.SUCCESS;
    }
}
