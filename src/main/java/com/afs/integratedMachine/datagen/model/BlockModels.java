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
        //refining furnace
        ModelFile refiningFurnace = simpleMachineModel("refining_furnace", modBlockLoc("refining_furnace"));
        ModelFile refiningFurnaceLit = simpleMachineModel("refining_furnace_lit", modBlockLoc("refining_furnace"), "_lit");
        VariantBlockStateBuilder refiningFurnaceBlockStates = getVariantBuilder(IMBlocks.REFINING_FURNACE.get());
        refiningFurnaceBlockStates.forAllStates(
                state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(BlockStateProperties.LIT)?refiningFurnaceLit:refiningFurnace)
                        .rotationY(((int)state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                        .build()
        );
        simpleBlockItem(IMBlocks.REFINING_FURNACE.get(), refiningFurnace);
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
