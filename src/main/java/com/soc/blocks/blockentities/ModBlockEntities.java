package com.soc.blocks.blockentities;

import com.soc.SocWars;
import com.soc.blocks.util.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static void initialise() {

    }

    public static final BlockEntityType<CollectibleBlockEntity> COLLECTIBLE_BLOCK_ENTITY = register("collectible_block", CollectibleBlockEntity::new, ModBlocks.COLLECTIBLE_BLOCK);
    public static final BlockEntityType<MapBlockEntity> MAP_BLOCK_ENTITY = ModBlockEntities.register("map_block", MapBlockEntity::new, ModBlocks.MAP_BLOCK);

    public static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.of(SocWars.MOD_ID, name);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }
}
