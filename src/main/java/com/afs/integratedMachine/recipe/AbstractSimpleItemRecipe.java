package com.afs.integratedMachine.recipe;

import com.afs.integratedMachine.recipe.input.SimpleItemInput;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractSimpleItemRecipe implements Recipe<SimpleItemInput> {
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }
}
