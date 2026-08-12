package com.afs.integratedMachine.block.entity;

import com.afs.integratedMachine.block.CompartmentControllerBlock;
import com.afs.integratedMachine.block.utils.IMBlockStateProperties;
import com.afs.integratedMachine.compartment.Compartment;
import com.afs.integratedMachine.compartment.CompartmentRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

public class CompartmentControllerBlockEntity extends BlockEntity {
    private CompartmentRef compartment = CompartmentRef.EMPTY;
    private Block displayBlock = Blocks.AIR;

    @Override
    public void onLoad() {
        if(compartment == CompartmentRef.EMPTY){
            tryCreateCompartment();
        }
    }

    public CompartmentControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(IMBlockEntityTypes.COMPARTMENT_CONTROLLER.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        compartment = CompartmentRef.load(tag.getCompound("compartment"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("compartment", compartment.save());
    }

    public boolean tryCreateCompartment(){
        if(level == null || level.isClientSide){
            return false;
        }
        BlockState state = level.getBlockState(getBlockPos());
        if(state.getValue(IMBlockStateProperties.ACTIVE)){
            return false;
        }
        CompartmentControllerBlock block = (CompartmentControllerBlock) state.getBlock();
        BlockPos begin = getBlockPos().offset(state.getValue(BlockStateProperties.FACING).getNormal());
        Optional<Compartment> compartment = Compartment.tryCreateCompartment(level, getBlockPos(), begin, block.getMaxDistance(), block.getMaxCount());
        if(compartment.isPresent()){
            Compartment c = compartment.get();
            this.compartment = c.addToChunk(level);
            level.setBlock(getBlockPos(), state.setValue(IMBlockStateProperties.ACTIVE, true), Block.UPDATE_CLIENTS);
            return true;
        }
        return false;
    }
}
