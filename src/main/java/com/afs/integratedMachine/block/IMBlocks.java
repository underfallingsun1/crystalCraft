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

    public static final DeferredRegister<MapCodec<? extends Block>> TYPES = DeferredRegister.create(
            Registries.BLOCK_TYPE, Meta.MODID
    );
}
