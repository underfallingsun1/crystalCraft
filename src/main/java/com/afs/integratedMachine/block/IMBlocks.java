package com.afs.integratedMachine.block;

import com.afs.integratedMachine.utils.Meta;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.sql.Ref;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class IMBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Meta.MODID);

    public static final DeferredBlock<RefiningFurnaceBlock> REFINING_FURNACE = BLOCKS.registerBlock(
            "refining_block", RefiningFurnaceBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(4.0f, 8.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(BlockStateProperties.LIT)?15:0)
    );

    public static final DeferredRegister<MapCodec<? extends Block>> TYPES = DeferredRegister.create(
            Registries.BLOCK_TYPE, Meta.MODID
    );

    public static final Supplier<MapCodec<? extends Block>> REFINING_FURNACE_TYPE = TYPES.register(
            "refining_furnace", () -> RefiningFurnaceBlock.CODEC
    );
}
