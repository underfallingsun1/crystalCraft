package com.afs.integratedMachine.recipe;

import com.afs.integratedMachine.recipe.input.MachineRecipeInput;
import com.afs.integratedMachine.recipe.utils.inputEntry.AbstractInputEntry;
import com.afs.integratedMachine.recipe.utils.outputEntry.AbstractOutputEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class AbstractMachineRecipe<I extends MachineRecipeInput> implements Recipe<I> {
    @Override
    public boolean matches(I input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(I input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public abstract List<AbstractInputEntry> getInputEntries();

    public abstract List<AbstractInputEntry> getInputEntries(AbstractInputEntry.InputType type);

    public abstract List<AbstractOutputEntry> getOutputEntries();

    public abstract List<AbstractOutputEntry> getOutputEntries(AbstractOutputEntry.OutputType type);


}
