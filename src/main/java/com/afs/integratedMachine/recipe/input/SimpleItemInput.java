package com.afs.integratedMachine.recipe.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandler;

public record SimpleItemInput(IItemHandler handler) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return handler.getStackInSlot(index);
    }

    @Override
    public int size() {
        return handler.getSlots();
    }
}
