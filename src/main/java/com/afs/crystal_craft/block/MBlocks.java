package com.afs.crystal_craft.block;

import com.afs.crystal_craft.utils.Utils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(Utils.MODID);
    public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister.create(
            Registries.BLOCK_TYPE, Utils.MODID
    );
}
