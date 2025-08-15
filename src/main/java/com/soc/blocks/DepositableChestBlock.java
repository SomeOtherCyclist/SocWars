package com.soc.blocks;

import com.soc.blocks.util.ModBlocks;
import com.soc.items.util.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemGroups;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class DepositableChestBlock extends ChestBlock {
    public DepositableChestBlock(Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier, Settings settings) {
        super(blockEntityTypeSupplier, settings);
    }

    public static void initialise() {
        ModItems.addItemToGroups(DEPOSITABLE_CHEST.asItem(), ItemGroups.FUNCTIONAL);
    }

    public static final Block DEPOSITABLE_CHEST = ModBlocks.register(
            "depositable_chest",
            settings -> new DepositableChestBlock(() -> BlockEntityType.CHEST, settings),
            AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable(),
            true
    );

    @Override
    protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        Inventory inventory = getInventory(this, state, world, pos, false);
        assert inventory != null;
        String a = inventory.iterator().next().getName().toString();

        player.sendMessage(Text.literal("Clicked. First item: " + a), false);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChestBlockEntity(pos, state);
    }
}
