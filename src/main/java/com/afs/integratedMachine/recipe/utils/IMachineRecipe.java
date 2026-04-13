package com.afs.integratedMachine.recipe.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public interface IMachineRecipe<T extends BlockEntity> {
    List<ItemStack> takeItem(IItemHandler handler, int repeat);

    default List<ItemStack> getItemOutput(RecipeInput input, T entity){
        return List.of();
    }

    default List<FluidStack> getFluidStack(RecipeInput input ,T entity){
        return List.of();
    }

    default RecipeEvent getRecipeEvent(RecipeInput input, T entity){
        return RecipeEvent.EMPTY;
    }

    default List<ItemStack> useItem(IItemHandler handler, int repeat){
        return List.of();
    }

    default List<FluidStack> useFluid(IFluidHandler handler, int repeat){
        return List.of();
    }

    default boolean checkItemOutput(IItemHandler handler, int repeat){
        return true;
    }

    default boolean checkFluidOutput(IFluidHandler handler, int repeat){
        return true;
    }

    default boolean checkOutput(IItemHandler itemHandler, IFluidHandler fluidHandler, int repeat){
        return checkItemOutput(itemHandler, repeat) && checkFluidOutput(fluidHandler, repeat);
    }

    @FunctionalInterface
    interface RecipeEvent{
        void run(Level level, BlockPos pos);

        RecipeEvent EMPTY = (level, pos) -> {};
    }
}
