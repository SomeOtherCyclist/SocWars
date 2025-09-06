package com.soc.renderstate;

import com.soc.blocks.util.ModBlocks;
import com.soc.entities.BigTntEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class BigTntRenderState extends EntityRenderState {
    public BigTntRenderState(BigTntEntity.BigTntType tntType) {
        this.tntType = tntType;
    }

    private BigTntEntity.BigTntType tntType;
    public float fuse = 0f;

    public void setTntType(BigTntEntity.BigTntType tntType) {
        this.tntType = tntType;
    }
    
    public BlockState getBlockState() {
        return (switch (this.tntType) {
            case NUCLEAR -> ModBlocks.NUCLEAR_BOMB;
            case HYDROGEN -> ModBlocks.HYDROGEN_BOMB;
        }).getDefaultState();
    }
}
