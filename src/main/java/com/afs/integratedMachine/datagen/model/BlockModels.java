package com.afs.integratedMachine.datagen.model;

import com.afs.integratedMachine.block.IMBlocks;
import com.afs.integratedMachine.utils.Meta;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockModels extends BlockStateProvider {
    public BlockModels(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Meta.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
    }

    private ResourceLocation modBlockLoc(String path){
        return modLoc("block/" + path);
    }

    private ModelFile simpleMachineModel(String name, ResourceLocation path){
        return simpleMachineModel(name, path, "");
    }

    private ModelFile simpleMachineModel(String name, ResourceLocation path, String suffix){
        ResourceLocation sidePath = path.withSuffix("/side");
        ResourceLocation facePath = path.withSuffix("/face" + suffix);
        return models().cube(name, sidePath, sidePath, facePath, sidePath, sidePath, sidePath).texture("particle", sidePath);
    }
}
