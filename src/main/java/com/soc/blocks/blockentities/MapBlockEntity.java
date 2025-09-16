package com.soc.blocks.blockentities;

import com.mojang.serialization.Codec;
import com.soc.SocWars;
import com.soc.blocks.ColourStateBlock;
import com.soc.game.manager.GameType;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.BedwarsGameMap;
import com.soc.game.map.MapCheckResults;
import com.soc.game.map.SkywarsGameMap;
import com.soc.lib.InfoList;
import com.soc.util.BlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;

import static com.soc.blocks.blockentities.ModBlockEntities.MAP_BLOCK_ENTITY;

public class MapBlockEntity extends BlockEntity {
    public static final int X_COLOUR = 0xdff21f43;
    public static final int Y_COLOUR = 0xdf1ff24d;
    public static final int Z_COLOUR = 0xdf1f3ff2;

    private BlockPos.Mutable regionSize;
    private String mapName;
    private GameType mapType;

    private MapCheckResults mapCheckResults = null;
    private InfoList mapCheckInfo = new InfoList();

    public MapBlockEntity(BlockPos pos, BlockState state) {
        super(MAP_BLOCK_ENTITY, pos, state);

        this.regionSize = new BlockPos.Mutable(1, 1, 1);
        this.mapName = "";
        this.mapType = GameType.SKYWARS;
    }

    public void checkStructure() {
        //General
        HashSet<Pair<Integer, BlockPos>> spawnPositions = new HashSet<>();
        HashSet<BlockPos> centrePositions = new HashSet<>();

        //Bedwars
        HashSet<BlockPos> diamondGens = new HashSet<>();
        HashSet<BlockPos> emeraldGens = new HashSet<>();
        HashSet<BlockPos> islandGens = new HashSet<>();

        final BlockPos minPos = this.getPos();
        final BlockPos maxPos = this.getPos().add(this.regionSize);
        for (int x = minPos.getX(); x < maxPos.getX(); x++) {
            for (int y = minPos.getY() + 1; y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z < maxPos.getZ(); z++) {
                    final BlockPos currentPos = new BlockPos(x, y, z);
                    final BlockState blockState = this.world.getBlockState(currentPos);

                    if (blockState.streamTags().anyMatch(BlockTags.MAP_PLACEHOLDER::equals)) {
                        switch (blockState.getBlock().getName().getString()) {
                            case "Spawn Placeholder" -> spawnPositions.add(Pair.of(blockState.get(ColourStateBlock.COLOUR), currentPos));
                            case "Centre Placeholder" -> centrePositions.add(currentPos);
                            case "Diamond Generator Placeholder" -> diamondGens.add(currentPos);
                            case "Emerald Generator Placeholder" -> emeraldGens.add(currentPos);
                            case "Island Generator Placeholder" -> islandGens.add(currentPos);
                            default -> SocWars.LOGGER.warn("Looks like someone accidentally assigned the map_placeholder tag to something that it shouldn't be assigned to");
                        }
                    }
                }
            }
        }

        this.mapCheckResults = new MapCheckResults(spawnPositions, centrePositions, diamondGens, emeraldGens, islandGens);
        this.mapCheckInfo = mapCheckResults.generateInfo(this.mapType);
    }

    public boolean saveMap(ServerPlayerEntity player) {
        this.checkStructure();
        if (this.mapCheckInfo.hasErrors() || this.world.isClient) return false;

        /*
        StructureTemplate structure = ((ServerWorld)this.world).getStructureTemplateManager().getTemplate()

        AbstractGameMap map = switch (this.mapType) {
            case SKYWARS -> new SkywarsGameMap(

            );
            case BEDWARS -> new BedwarsGameMap(

            );
            case PROP_HUNT -> null;
        }
         */


        player.sendMessage(Text.translatable("map_block.save_success", this.mapName, this.mapType.getFileExtension()));
        return true;
    }

    @Override
    protected void writeData(WriteView view) {
        view.put("region_size", BlockPos.Mutable.CODEC, this.regionSize);
        view.put("map_name", Codec.STRING, this.mapName);
        view.put("map_type", Codec.INT, this.mapType.ordinal());

        super.writeData(view);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        this.regionSize = view.read("region_size", BlockPos.Mutable.CODEC).orElse(new BlockPos.Mutable(1, 1, 1)).mutableCopy();
        this.mapName = view.read("map_name", Codec.STRING).orElse("");
        this.mapType = GameType.fromOrdinal(view.read("map_type", Codec.INT).orElse(0));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
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

    public InfoList getMapCheckInfo(GameType mapType) {
        return this.mapCheckResults == null ? new InfoList() : this.mapCheckResults.generateInfo(mapType);
    }
}
