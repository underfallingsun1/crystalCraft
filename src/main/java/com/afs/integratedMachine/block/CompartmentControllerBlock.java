package com.afs.integratedMachine.block;

import com.afs.integratedMachine.block.entity.CompartmentControllerBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CompartmentControllerBlock extends Block implements EntityBlock {
    private final int maxCount;
    private final int maxDistance;

    public CompartmentControllerBlock(Properties properties, int maxCount, int maxDistance) {
        super(properties);
        this.maxCount = maxCount;
        this.maxDistance = maxDistance;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CompartmentControllerBlockEntity(pos, state);
    }

    public static final MapCodec<CompartmentControllerBlock> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    BlockBehaviour.propertiesCodec(),
                    Codec.INT.fieldOf("max_count").forGetter(CompartmentControllerBlock::getMaxCount),
                    Codec.INT.fieldOf("max_distance").forGetter(CompartmentControllerBlock::getMaxDistance)
            ).apply(inst, CompartmentControllerBlock::new)
    );

    public int getMaxCount() {
        return maxCount;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return IMBlocks.COMPARTMENT_CONTROLLER_BLOCK_CODEC.get();
    }
}
