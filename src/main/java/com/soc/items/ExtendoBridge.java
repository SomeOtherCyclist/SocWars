package com.soc.items;

import com.soc.items.util.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class ExtendoBridge extends Item {
    private static final Block[] BLOCKS = {
            Blocks.OAK_PLANKS,
            Blocks.BIRCH_PLANKS,
            Blocks.JUNGLE_PLANKS,
            Blocks.SPRUCE_PLANKS,
            Blocks.DARK_OAK_PLANKS,
            Blocks.ACACIA_PLANKS,
            Blocks.MANGROVE_PLANKS,
            Blocks.PALE_OAK_PLANKS,
            Blocks.WARPED_PLANKS,
            Blocks.CRIMSON_PLANKS,
            Blocks.BAMBOO_PLANKS,
            Blocks.CHERRY_PLANKS
    };

    public ExtendoBridge(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        ModItems.addItemToGroups(EXTENDO_BRIDGE, ItemGroups.TOOLS);
    }

    public static final Item EXTENDO_BRIDGE = ModItems.register("extendo_bridge", ExtendoBridge::new, new Settings().rarity(Rarity.UNCOMMON).useCooldown(0.25f));

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        BlockState block = BLOCKS[world.random.nextBetween(0, BLOCKS.length - 1)].getDefaultState();

        float lengthMarched = 0f;

        Vec3d direction = user.getRotationVector();
        direction = direction.multiply(1f, 0f, 1f).normalize();

        Vec3d currentPosition = new Vec3d(user.getX() - 0.5f, Math.floor(user.getY() - 0.75f), user.getZ() - 0.5f);

        float stepSize = 0.1f;

        while (lengthMarched < 16f) {
            BlockPos pos = new BlockPos((int)currentPosition.x, (int)currentPosition.y, (int)currentPosition.z);
            if (world.getBlockState(pos) == Blocks.AIR.getDefaultState()) {
                world.setBlockState(pos, block);
            }

            currentPosition = currentPosition.add(direction.multiply(stepSize));
            lengthMarched += stepSize;
        }

        user.getStackInHand(hand).decrementUnlessCreative(1, user);

        return ActionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("tooltip.extendo_bridge"));
    }
}
