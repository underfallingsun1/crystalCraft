package com.afs.integratedMachine.recipe.input;

import com.afs.integratedMachine.recipe.utils.outputEntry.OutputEntryEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Map;

public interface MachineRecipeInput extends RecipeInput {
    void setItemStack(int i, ItemStack stack);
    int getFluidSize();
    FluidStack getFluid(int i);
    void setFluidStack(int i, ItemStack stack);
    Map<String, Integer> getConfig();
    void runEvent(OutputEntryEffect.Event<? extends BlockEntity> event);

    @Override
    default boolean isEmpty() {
        if(RecipeInput.super.isEmpty()){
            for(int i = 0;i < getFluidSize();i++){
                if(!getFluid(i).isEmpty()){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
