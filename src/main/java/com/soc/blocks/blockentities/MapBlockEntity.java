package com.soc.blocks.blockentities;

import com.mojang.serialization.Codec;
import com.soc.SocWars;
import com.soc.blocks.ModifiableColourStateBlock;
import com.soc.blocks.TierBlock;
import com.soc.blocks.util.ModBlocks;
import com.soc.game.manager.GameType;
import com.soc.game.map.*;
import com.soc.lib.CubicList;
import com.soc.lib.InfoList;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SkywarsChest;
import com.soc.nbt.SpawnPosition;
import com.soc.util.BlockTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.soc.blocks.blockentities.ModBlockEntities.MAP_BLOCK_ENTITY;
import static com.soc.blocks.util.ModBlocks.*;
import static com.soc.game.map.AbstractGameMap.getMapDirectory;
import static com.soc.lib.SocWarsLib.iterateInCube;

public class MapBlockEntity extends BlockEntity {
    public static final int X_COLOUR = 0xdff21f43;
    public static final int Y_COLOUR = 0xdf1ff24d;
    public static final int Z_COLOUR = 0xdf1f3ff2;

    public static final List<Block> IGNORED_BLOCKS = List.of( //Maybe do something with the map_placeholder tag
            SPAWN_PLACEHOLDER,
            CENTRE_PLACEHOLDER,
            DIAMOND_GEN_PLACEHOLDER,
            EMERALD_GEN_PLACEHOLDER,
            ISLAND_GEN_PLACEHOLDER,
            CHEST_PLACEHOLDER,
            POWERUP_PLACEHOLDER,
            INDIVIDUAL_SHOP_PLACEHOLDER,
            TEAM_SHOP_PLACEHOLDER,
            Blocks.AIR,
            PROTECTED_AIR
    );
    public static final Codecs.StrictUnboundedMapCodec<String, Integer> FIELDS_CODEC = Codecs.strictUnboundedMap(Codec.STRING, Codec.INT);

    private BlockPos.Mutable regionSize;
    private String mapName;
    private GameType mapType;
    private boolean blockProtection;
    private Map<String, Integer> fields;

    private MapCheckResults mapCheckResults = null;
    private InfoList mapCheckInfo = new InfoList();

    public MapBlockEntity(BlockPos pos, BlockState state) {
        super(MAP_BLOCK_ENTITY, pos, state);

        this.regionSize = new BlockPos.Mutable(1, 1, 1);
        this.mapName = "";
        this.mapType = GameType.SKYWARS;
        this.blockProtection = false;
        this.fields = new HashMap<>();
    }

    public void checkStructure() {
        //General
        final HashSet<SpawnPosition> spawnPositions = new HashSet<>();
        final HashSet<BlockPos> centrePositions = new HashSet<>();
        final HashSet<Direction> flaggedFaces = new HashSet<>();

        //Bedwars
        final HashSet<BlockPos> diamondGens = new HashSet<>();
        final HashSet<BlockPos> emeraldGens = new HashSet<>();
        final HashSet<BlockPos> islandGens = new HashSet<>();
        final HashSet<BlockPos> bedPositions = new HashSet<>();
        final HashSet<BlockPos> individualShops = new HashSet<>();
        final HashSet<BlockPos> teamShops = new HashSet<>();

        //Skywars
        final HashSet<SkywarsChest> lootChests = new HashSet<>();

        //Hiding
        final HashSet<BlockPos> powerups = new HashSet<>();

        //region Main structure check
        final BlockPos minPos = this.getPos().add(0, 1, 0);
        final BlockPos maxPos = minPos.add(this.regionSize);

        iterateInCube(minPos, maxPos, pos -> {
            final BlockState blockState = this.world.getBlockState(pos);
            if (!blockState.isIn(BlockTags.MAP_PLACEHOLDER)) return;

            final Block block = blockState.getBlock();
            if (block == SPAWN_PLACEHOLDER) spawnPositions.add(new SpawnPosition(pos, blockState.get(ModifiableColourStateBlock.COLOUR).ordinal()));
            else if (block == CENTRE_PLACEHOLDER) centrePositions.add(pos);
            else if (block == DIAMOND_GEN_PLACEHOLDER) diamondGens.add(pos);
            else if (block == EMERALD_GEN_PLACEHOLDER) emeraldGens.add(pos);
            else if (block == ISLAND_GEN_PLACEHOLDER) islandGens.add(pos);
            else if (block == INDIVIDUAL_SHOP_PLACEHOLDER) individualShops.add(pos);
            else if (block == TEAM_SHOP_PLACEHOLDER) teamShops.add(pos);
            else if (block == CHEST_PLACEHOLDER) {
                final BlockState state = world.getBlockState(pos);
                lootChests.add(new SkywarsChest(pos, state.get(TierBlock.TIER), state.get(HorizontalFacingBlock.FACING).getOpposite()));
            }

            else if (block == POWERUP_PLACEHOLDER) powerups.add(pos);
            else {
                if (blockState.getBlock() instanceof BedBlock && blockState.get(BedBlock.PART) == BedPart.HEAD) {
                    bedPositions.add(pos);
                    return;
                }
                SocWars.LOGGER.warn("Looks like someone accidentally assigned the map_placeholder tag to something that it shouldn't be assigned to");
            }
        });
        //endregion

        //region Face bordering checks
        for (int x: new int[] {minPos.getX() - 1, maxPos.getX()}) {
            edgeXLoop:
            for (int y = minPos.getY() - 1; y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z < maxPos.getZ(); z++) {
                    final BlockPos currentPos = new BlockPos(x, y, z);

                    if (!this.world.isAir(currentPos)) {
                        if (x == minPos.getX() - 1) {
                            flaggedFaces.add(Direction.WEST);
                        } else {
                            flaggedFaces.add(Direction.EAST);
                        }
                        break edgeXLoop;
                    }
                }
            }
        }

        for (int y: new int[] {minPos.getY() - 1, maxPos.getY()}) {
            edgeYLoop:
            for (int z = minPos.getZ() - 1; z <= maxPos.getZ(); z++) {
                for (int x = minPos.getX(); x < maxPos.getX(); x++) {
                    final BlockPos currentPos = new BlockPos(x, y, z);

                    if (!(this.world.isAir(currentPos) || this.world.getBlockState(currentPos).isOf(ModBlocks.MAP_BLOCK))) {
                        if (y == minPos.getY() - 1) {
                            flaggedFaces.add(Direction.DOWN);
                        } else {
                            flaggedFaces.add(Direction.UP);
                        }
                        break edgeYLoop;
                    }
                }
            }
        }

        for (int z: new int[] {minPos.getZ() - 1, maxPos.getZ()}) {
            edgeZLoop:
            for (int x = minPos.getX() - 1; x <= maxPos.getX(); x++) {
                for (int y = minPos.getY(); y < maxPos.getY(); y++) {
                    final BlockPos currentPos = new BlockPos(x, y, z);

                    if (!this.world.isAir(currentPos)) {
                        if (z == minPos.getZ() - 1) {
                            flaggedFaces.add(Direction.NORTH);
                        } else {
                            flaggedFaces.add(Direction.SOUTH);
                        }
                        break edgeZLoop;
                    }
                }
            }
        }
        //endregion
        this.mapCheckResults = new MapCheckResults(spawnPositions, centrePositions, flaggedFaces, diamondGens, emeraldGens, islandGens, bedPositions, individualShops, teamShops, lootChests, powerups);
        this.mapCheckInfo = this.mapCheckResults.generateInfo(this.mapType);
    }

    public boolean saveMap(ServerPlayerEntity player) {
        this.checkStructure();
        if (this.mapCheckInfo.hasErrors() || this.world.isClient) return false;

        final StructureTemplate structure = new StructureTemplate();
        structure.saveFromWorld(this.world, this.pos.up(), this.regionSize, true, IGNORED_BLOCKS);
        final BlockPos centrePos = this.mapCheckResults.centrePositions().stream().findAny().orElse(new BlockPos(0, 0, 0)).subtract(this.pos).down();

        final SparseVoxelOctree<Boolean> blockProtectionOverlay;
        if (this.blockProtection) {
            final BlockPos origin = this.pos.up();
            final CubicList<Boolean> naive = new CubicList<>(structure.getSize(), (x, y, z) -> {
                final BlockState state = this.world.getBlockState(origin.add(x, y, z));
                return !(state.isAir() || state.isIn(net.minecraft.registry.tag.BlockTags.BEDS) || state.isReplaceable());
            });
            blockProtectionOverlay = naive.asOctree();
        } else {
            blockProtectionOverlay = null;
        }

        final AbstractGameMap map = switch (this.mapType) {
            case SKYWARS -> new SkywarsGameMap(
                    structure,
                    this.mapCheckResults.getRelativeGeneric(this.mapCheckResults.spawnPositions()),
                    centrePos,
                    blockProtectionOverlay,
                    this.mapCheckResults.getRelativeGeneric(this.mapCheckResults.lootChests()),
                    this.fields
            );
            case BEDWARS -> new BedwarsGameMap(
                    structure,
                    this.mapCheckResults.getRelativeGeneric(this.mapCheckResults.spawnPositions()),
                    centrePos,
                    blockProtectionOverlay,
                    this.mapCheckResults.getRelative(this.mapCheckResults.diamondGens()),
                    this.mapCheckResults.getRelative(this.mapCheckResults.emeraldGens()),
                    this.mapCheckResults.getRelative(this.mapCheckResults.islandGens()),
                    this.mapCheckResults.getRelative(this.mapCheckResults.bedPositions()),
                    this.mapCheckResults.getRelative(this.mapCheckResults.individualShops()),
                    this.mapCheckResults.getRelative(this.mapCheckResults.teamShops()),
                    this.fields
            );
            case PROP_HUNT -> new PropHuntGameMap(
                    structure,
                    this.mapCheckResults.getRelativeGeneric(this.mapCheckResults.spawnPositions()),
                    centrePos,
                    blockProtectionOverlay,
                    this.fields
            );
            case HIDE_AND_SEEK -> new HideAndSeekGameMap(
                    structure,
                    this.mapCheckResults.getRelativeGeneric(this.mapCheckResults.spawnPositions()),
                    centrePos,
                    blockProtectionOverlay,
                    this.mapCheckResults.getRelative(this.mapCheckResults.powerups()),
                    this.fields
            );
        };

        final NbtCompound mapNbt = map.toNbt(new NbtCompound());

        try {
            NbtIo.write(mapNbt, Path.of(getMapDirectory().toString(), String.format("%s.%s", this.mapName, this.mapType.getFileExtension())));
        } catch (IOException e) {
            SocWars.LOGGER.error("Failed to write {}.{} to file", this.mapName, this.mapType.getFileExtension());
            return false;
        }

        player.sendMessage(Text.translatable("map_block.save_success", this.mapName, this.mapType.getFileExtension()));
        return true;
    }

    @Override
    protected void writeData(WriteView view) {
        view.put("region_size", BlockPos.Mutable.CODEC, this.regionSize);
        view.put("map_name", Codec.STRING, this.mapName);
        view.put("map_type", Codec.INT, this.mapType.ordinal());
        view.put("block_protection", Codec.BOOL, this.blockProtection);
        view.put("fields", FIELDS_CODEC, this.fields);
    }

    @Override
    protected void readData(ReadView view) {
        this.regionSize = view.read("region_size", BlockPos.Mutable.CODEC).orElse(new BlockPos.Mutable(1, 1, 1)).mutableCopy();
        this.mapName = view.read("map_name", Codec.STRING).orElse("");
        this.mapType = GameType.fromOrdinal(view.read("map_type", Codec.INT).orElse(0));
        this.blockProtection = view.read("block_protection", Codec.BOOL).orElse(false);
        this.fields = new HashMap<>(view.read("fields", FIELDS_CODEC).orElse(Map.of()));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return super.createNbt(registryLookup);
    }

    public BlockPos.Mutable getRegionSize() {
        this.markDirty();
        return this.regionSize;
    }
    public void setRegionSize(BlockPos.Mutable regionSize) {
        this.regionSize = regionSize;
        this.markDirty();
    }

    public String getMapName() {
        return this.mapName;
    }
    public void setMapName(String name) {
        this.mapName = name;
        this.markDirty();
    }

    public GameType getMapType() {
        return this.mapType;
    }
    public void setMapType(GameType mapType) {
        this.mapType = mapType;
        this.markDirty();
    }

    public boolean hasBlockProtection() {
        return this.blockProtection;
    }
    public void setBlockProtection(boolean enabled) {
        this.blockProtection = enabled;
        this.markDirty();
    }

    public Map<String, Integer> getFields() {
        return this.fields;
    }

    public void setFields(Map<String, Integer> fields) {
        this.fields = fields;
        this.markDirty();
    }

    public InfoList getMapCheckInfo(GameType mapType) {
        return this.mapCheckResults == null ? new InfoList() : this.mapCheckResults.generateInfo(mapType);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.getWorld() instanceof ServerWorld serverWorld) serverWorld.getChunkManager().markForUpdate(this.getPos());
    }
}
