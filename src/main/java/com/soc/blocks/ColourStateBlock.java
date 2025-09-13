package com.soc.blocks;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class ColourStateBlock extends Block {
    public static final MapCodec<ColourStateBlock> CODEC = createCodec(ColourStateBlock::new);
    public static final IntProperty COLOUR = IntProperty.of("dye_colour_with_empty", 0, 16);

    @Override
    public MapCodec<? extends ColourStateBlock> getCodec() {
        return CODEC;
    }

    public ColourStateBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(COLOUR, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOUR);
    }
    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.isCreative()) return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;

        if (player.isSneaking()) {
            world.setBlockState(pos, state.with(COLOUR, 16));

            return ActionResult.SUCCESS;
        }

        final Optional<TagKey<Item>> dyedTag = stack.streamTags().filter(tag -> tag.toString().contains("dyed/")).findFirst();
        if (dyedTag.isEmpty()) return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;


        try {
            final String tagString = dyedTag.toString().split("/")[2].replace("]", "");
            world.setBlockState(pos, state.with(COLOUR, DyeColor.valueOf(tagString.toUpperCase()).ordinal()));

            return ActionResult.SUCCESS;
        } catch (IllegalArgumentException e) {
            return ActionResult.FAIL;
        }
    }
}
