package com.afs.integratedMachine.recipe.utils.inputEntry;

import com.afs.integratedMachine.recipe.utils.randomSet.RandomSetHandle;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.Map;

public class InputEntryEffect {
    public interface Item{
        Ingredient getIngredient();
        int getCount();
        int getConsumedAmount(Map<String, Integer> config, RandomSetHandle randomSets);
    }

    public interface Fluid{
        FluidIngredient getIngredient();
        int getAmount();
        int getConsumedAmount(Map<String, Integer> config, RandomSetHandle randomSets);
    }

    public interface Environment{
        boolean test(Map<String, Integer> config, RandomSetHandle randomSets);
    }
}
