package com.soc.game.map;

import com.soc.SocWars;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SkywarsChest;
import com.soc.nbt.SpawnPosition;
import com.soc.resourcedata.listeners.SkywarsLootData;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.woolItemFromColour;

public class SkywarsGameMap extends AbstractGameMap {
    public static final String FILE_EXTENSION = "swmap";

    private final Map<BlockPos, IngameSkywarsChest> lootChests;

    public SkywarsGameMap(
            StructureTemplate structure,
            Set<SpawnPosition> spawnPositions,
            BlockPos centrePos,
            BlockPos absoluteCentrePos,
            SparseVoxelOctree<Boolean> blockProtectionOverlay,
            int minBuildY,
            int maxBuildY,
            ServerWorld world,
            Set<SkywarsChest> lootChests,
            File file
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, blockProtectionOverlay, minBuildY, maxBuildY, world, file);
        this.lootChests = lootChests.stream().collect(Collectors.toMap(chest -> super.pos(chest.pos()), IngameSkywarsChest::new));
    }

    /// Constructor used only for saving the map to file
    public SkywarsGameMap(
            StructureTemplate structure,
            Set<SpawnPosition> spawnPositions,
            BlockPos centrePos,
            @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
            Set<SkywarsChest> lootChests,
            Map<String, Integer> fields) {
        super(structure, spawnPositions, centrePos, blockProtectionOverlay);
        this.lootChests = lootChests.stream().collect(Collectors.toMap(chest -> super.pos(chest.pos()), IngameSkywarsChest::new));
    }

    public void placeLootChests() {
        this.lootChests.forEach((pos, chest) -> {
            this.world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(HorizontalFacingBlock.FACING, chest.getFacing()));

            final Inventory inventory = ChestBlock.getInventory((ChestBlock) Blocks.CHEST, this.world.getBlockState(pos), this.world, pos, true);
            if (inventory != null) {
                this.populateInventory(inventory, chest.getTier(), pos);
            } else {
                SocWars.LOGGER.warn("Failed to populate chest at {}", pos);
            }
        });
    }

    private void populateInventory(Inventory inventory, int tier, BlockPos pos) {
        inventory.clear();
//        for (int i = 0; i < inventory.size(); i++) {
//            final float random = this.world.random.nextFloat(); //TODO: Redo all of this code because it's awful; probably take from data
//            if (random > 0.8f + tier * 0.02f) {
//                final float random2 = this.world.random.nextFloat();
//                final int pool = random2 < 0.55f + tier * 0.04f ? 0 : 1;
//
//                final ItemStack stack = SkywarsLootData.INSTANCE.getSkywarsItemData().getRandomItem(pool, tier, this.world.random);
//
//                if (stack.isIn(ItemTags.BOW_ENCHANTABLE)) stack.addEnchantment(this.world.getRegistryManager().getEntryOrThrow(Enchantments.INFINITY), 1);
//                inventory.setStack(i, stack);
//            }
//        }
        final List<ItemStack> chestItems = SkywarsLootData.INSTANCE.getSkywarsItemData().getChestItems(tier, this.world, inventory.size());
        for (int i = 0; i < chestItems.size(); i++) {
            inventory.setStack(i, chestItems.get(i));
        }

        if (tier == 0) {
            final Optional<DyeColor> colour = this.spawnPositions.entries().stream().min(Map.Entry.comparingByValue((a, b) -> {
                final double distA = super.pos(a).getSquaredDistance(pos);
                final double distB = super.pos(b).getSquaredDistance(pos);
                if (distA == distB) return 0;
                return distA < distB ? -1 : 1;
            })).map(Map.Entry::getKey);

            colour.ifPresent(woolColour -> {
                int slot = this.world.random.nextBetween(0, 26);
                inventory.setStack(slot, new ItemStack(woolItemFromColour(woolColour), 32));
            });
        }
    }

    public Optional<IngameSkywarsChest> getLootChest(BlockPos pos) {
        return Optional.ofNullable(this.lootChests.get(pos));
    }

    public static Optional<SkywarsGameMap> fromNbt(@NotNull NbtCompound compound, @NotNull ServerWorld world, @NotNull BlockPos centrePos, File file) {
        final StructureTemplateManager templateManager = world.getStructureTemplateManager();
        final Optional<NbtCompound> structureCompound = compound.getCompound(STRUCTURE_KEY);
        final StructureTemplate template = structureCompound.map(templateManager::createTemplate).orElse(null);

        final Optional<Long> centrePosLong = compound.getLong(CENTRE_POS_KEY);
        if (centrePosLong.isEmpty()) {
            SocWars.LOGGER.error("Failed to load centre position for map; aborting load");
            return Optional.empty();
        }

        final Set<SpawnPosition> spawns = compound.getListOrEmpty(SpawnPosition.LIST_KEY).stream().map(element -> new SpawnPosition(element.asCompound().orElseThrow())).collect(Collectors.toSet());
        final Set<SkywarsChest> chests = compound.getListOrEmpty(SkywarsChest.LIST_KEY).stream().map(element -> new SkywarsChest(element.asCompound().orElseThrow())).collect(Collectors.toSet());

        return Optional.of(new SkywarsGameMap(
                template,
                spawns,
                BlockPos.fromLong(centrePosLong.get()),
                centrePos,
                null,
                compound.getInt(MIN_BUILD_Y_KEY, 0) + centrePos.getY(),
                compound.getInt(MAX_BUILD_Y_KEY, 60) + centrePos.getY(),
                world,
                chests, file));
    }

    @Override
    public NbtCompound toNbt(NbtCompound compound) {
        super.toNbt(compound);

        compound.put(SkywarsChest.LIST_KEY, this.getChestsAsNbt());

        return compound;
    }

    private NbtList getChestsAsNbt() {
        final NbtList chests = new NbtList();
        this.lootChests.forEach((pos, chest) -> chests.add(new SkywarsChest(pos, chest).toNbt()));
        return chests;
    }

    @Override
    public void tick() {}
}