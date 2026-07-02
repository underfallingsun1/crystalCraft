package com.afs.integratedMachine.block.entity;

import com.afs.integratedMachine.block.IMBlocks;
import com.afs.integratedMachine.utils.Meta;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "null"})
public class IMBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BE_TYPES = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE, Meta.MODID
    );
}
