package com.afs.integratedMachine.recipe.utils.outputEntry;

import com.afs.integratedMachine.recipe.utils.IMachineRecipe;
import com.afs.integratedMachine.recipe.utils.randomSet.RandomSetHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;

public class OutputEntryEffect {
    public interface Item{
        void addItem(List<ItemStack> result, RandomSetHandle random, Map<String, Integer> config);
        void addPossibleItem(List<ItemStack> result);
    }

    public interface Fluid{
        void addFluid(List<FluidStack> result, RandomSetHandle random, Map<String, Integer> config);
        void addPossibleFluid(List<FluidStack> result);
    }

    public interface Event<T extends BlockEntity> extends IMachineRecipe.RecipeEvent<T> {
    }
}
