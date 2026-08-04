package com.afs.integratedMachine.block;

import com.afs.integratedMachine.utils.Meta;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class IMBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Meta.MODID);

    public static final BlockBehaviour.Properties MACHINE_BLOCK_PROPERTY =
            BlockBehaviour.Properties.of().strength(4.0f, 8.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .instrument(NoteBlockInstrument.BASS)
                    .isValidSpawn((state, level, pos, type) -> false);

    public static final DeferredBlock<CompartmentControllerBlock> BASIC_COMPARTMENT_CONTROLLER =
            BLOCKS.registerBlock("basic_compartment_controller", p -> new CompartmentControllerBlock(p, 64, 8), MACHINE_BLOCK_PROPERTY);

    public static final DeferredRegister<MapCodec<? extends Block>> TYPES = DeferredRegister.create(
            Registries.BLOCK_TYPE, Meta.MODID
    );

    public static final Supplier<MapCodec<CompartmentControllerBlock>> COMPARTMENT_CONTROLLER_BLOCK_CODEC =
            TYPES.register("compartment_controller", () -> CompartmentControllerBlock.CODEC);
}
